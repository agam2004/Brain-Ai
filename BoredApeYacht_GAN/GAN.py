import self
import torch
import torch.optim as optim
from torchvision import transforms
from torch.utils.data import DataLoader
from torchvision.utils import save_image
from PIL import Image, UnidentifiedImageError
from torch.utils.data import Dataset
import os

# Generator definition
import torch.nn as nn


class Generator(nn.Module):
    def __init__(self, latent_dim, img_channels, img_size):
        super(Generator, self).__init__()
        self.init_size = img_size // 16  # Updated for 256x256 image size
        self.l1 = nn.Sequential(nn.Linear(latent_dim, 128 * self.init_size ** 2))

        self.conv_blocks = nn.Sequential(
            nn.BatchNorm2d(128),  # normalize the activations of the previous layer
            nn.Upsample(scale_factor=2),  # increase the spatial resolution of the feature maps
            nn.Conv2d(128, 128, 3, stride=1, padding=1),  #  apply a convolution operation
            nn.BatchNorm2d(128, 0.8),  # normalize the activations of the previous layer with a specified momentum.
            nn.LeakyReLU(0.2, inplace=True),  #  apply a non-linear activation function
            nn.Upsample(scale_factor=2),  # increase the spatial resolution of the feature maps
            nn.Conv2d(128, 64, 3, stride=1, padding=1),  # apply a convolution operation
            nn.BatchNorm2d(64, 0.8),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Upsample(scale_factor=2),
            nn.Conv2d(64, 64, 3, stride=1, padding=1),
            nn.BatchNorm2d(64, 0.8),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Upsample(scale_factor=2),
            nn.Conv2d(64, img_channels, 3, stride=1, padding=1),
            nn.Tanh()  # apply a final activation function
        )

    def forward(self, z):
        out = self.l1(z.view(z.size(0), -1))  #  apply the first linear transformation to the input latent vector
        out = out.view(out.shape[0], 128, self.init_size,
                       self.init_size)  # reshape the linear layer output into a 4D tensor suitable for convolutional layers
        img = self.conv_blocks(out)  # pass the reshaped tensor through a series of convolutional layers
        return img  # return the generated images


# Discriminator definition with feature extraction
class Discriminator(nn.Module):
    def __init__(self, img_channels, img_size):
        super(Discriminator, self).__init__()
        self.feature_extractor = nn.Sequential(
            nn.Conv2d(img_channels, 16, 3, 2, 1),  # 256 -> 128 Applies a 2D convolution over the input image
            nn.LeakyReLU(0.2, inplace=True),  # Applies the Leaky ReLU activation function with a negative slope of 0.2
            nn.Conv2d(16, 32, 3, 2, 1),  # 128 -> 64
            nn.BatchNorm2d(32),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Conv2d(32, 64, 3, 2, 1),  # 64 -> 32
            nn.BatchNorm2d(64),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Conv2d(64, 128, 3, 2, 1),  # 32 -> 16
            nn.BatchNorm2d(128),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Conv2d(128, 256, 3, 2, 1),  # 16 -> 8
            nn.BatchNorm2d(256),
            nn.LeakyReLU(0.2, inplace=True),
            nn.Conv2d(256, 512, 3, 2, 1),  # 8 -> 4
            nn.BatchNorm2d(512),
            nn.LeakyReLU(0.2, inplace=True),
        )
        # Defines the final layer that will output the validity score
        self.adv_layer = nn.Sequential(
            nn.Linear(512 * 4 * 4, 1),  # Correct calculation
        )

    # Defines the forward pass of the discriminator
    def forward(self, img):
        features = self.feature_extractor(img)
        out = features.view(features.size(0), -1)
        validity = self.adv_layer(out)
        return validity.view(-1), features


# Hyperparameters
latent_dim = 100  # Noise vector / generator's input
img_channels = 3  # Used to indicate the pixels as RGB
img_size = 256  # Height and width of the generated images
batch_size = 16  # Number of samples processed before the model's internal params are updated
lr_G = 0.0001  # Controls how much to change the model each time the model weights are updated
lr_D = 0.00005  # Controls the adjustment step size for the discriminator's weights
b1 = 0.5  # The exponential decay rate for the first moment estimates
b2 = 0.999  # The exponential decay rate for the second moment estimates
n_epochs = 20000  # Total number of training iterations over the entire dataset
sample_interval = 1  # How many samples from training to plot

# Prepare dataset
transform = transforms.Compose([
    transforms.Resize(img_size),
    transforms.ToTensor(),
    transforms.Normalize([0.5], [0.5])
])


class CustomDataset(Dataset):
    def __init__(self, root, transform=None):
        self.root = root
        self.transform = transform
        self.img_paths = [os.path.join(root, f) for f in os.listdir(root) if os.path.isfile(os.path.join(root, f))]

    def __len__(self):
        return len(self.img_paths)

    def __getitem__(self, idx):
        img_path = self.img_paths[idx]
        try:
            image = Image.open(img_path).convert("RGB")
        except UnidentifiedImageError:
            return self.__getitem__((idx + 1) % len(self.img_paths))  # Skip corrupted images
        if self.transform:
            image = self.transform(image)
        return image


class Generate:
    def __init__(self, latent_dim, img_channels, img_size):
        self.latent_dim = latent_dim
        self.generator = Generator(latent_dim, img_channels,
                                   img_size).cuda() if torch.cuda.is_available() else Generator(latent_dim,
                                                                                                img_channels, img_size)
        self.generator = self.load_generator("BoredApes/models/generator_256_imprv.pth")

    def load_generator(self, path):
        device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.generator.load_state_dict(torch.load(path, map_location=device))
        self.generator.eval()  # Set generator to evaluation mode
        return self.generator

    def generate_images(self, num_images, save_dir='BoredApes/generated_images'):
        os.makedirs(save_dir, exist_ok=True)
        noise = torch.randn(num_images, self.latent_dim).cuda() if torch.cuda.is_available() else torch.randn(
            num_images, self.latent_dim)
        generated_images = self.generator(noise)
        image_paths = []
        for i, image_tensor in enumerate(generated_images):
            image = transforms.ToPILImage()(image_tensor.cpu().detach())
            image_path = os.path.join(save_dir, f'generated_image_{i}.png')
            show_image = Image.open(image_path)
            show_image.show()
            image.save(image_path)
            image_paths.append(image_path)
        return image_paths


# Prepare dataset
dataset = CustomDataset(root=r'C:\Users\etai2\Downloads\Bored-Ape-Yacht\data', transform=transform)
dataloader = DataLoader(dataset, batch_size=batch_size, shuffle=True)

# Using label smoothing to balance the two parts
real_label_smooth = 0.9
fake_label_smooth = 0.1


def train_gan():
    # Check available devices
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print("Using device:", device)

    # Initialize generator and discriminator
    generator = Generator(latent_dim, img_channels, img_size).to(device)
    discriminator = Discriminator(img_channels, img_size).to(device)

    # Loading the trained Discriminator and Generator
    if os.path.exists("BoredApes/models/generator_new_model.pth"):
        generator.load_state_dict(torch.load("BoredApes/models/generator_new_model.pth", map_location=device))
        print("Generator model loaded.")

    if os.path.exists("BoredApes/models/discriminator_new_model.pth"):
        discriminator.load_state_dict(torch.load("BoredApes/models/discriminator_new_model.pth", map_location=device))
        print("Discriminator model loaded.")

    # Loss and optimizers
    adversarial_loss = nn.BCEWithLogitsLoss(reduction='sum')  # used for the adversarial loss(binary cross entropy)
    feature_matching_loss = nn.MSELoss()  # This is used for the feature matching loss
    optimizer_G = optim.Adam(generator.parameters(), lr=lr_G, betas=(
        b1, b2))  # Optimizers for the generator and discriminator with specified learning rates and beta parameters.
    optimizer_D = optim.Adam(discriminator.parameters(), lr=lr_D, betas=(
        b1, b2))  # # Optimizers for the generator and discriminator with specified learning rates and beta parameters.

    # Training
    for epoch in range(n_epochs):
        for i, imgs in enumerate(dataloader):
            imgs = imgs.to(device)

            valid = torch.ones(imgs.size(0), 1, requires_grad=False,
                               device=device)  # a tensor of ones representing real images.
            fake = torch.zeros(imgs.size(0), 1, requires_grad=False,
                               device=device)  # a tensor of zeros representing fake images

            real_imgs = imgs  # Used to assign the real images from the dataloader to real_imgs

            # Train Generator
            optimizer_G.zero_grad()  # To zero the gradients of the generator's optimizer
            z = torch.randn(imgs.size(0), latent_dim,
                            device=device)  # creates random noise vectors (z) with the shape (batch_size, latent_dim).
            gen_imgs = generator(z)  # The generator uses these noise vectors to generate fake images
            # get the discriminator's output and features for both real and fake images
            validity, real_features = discriminator(real_imgs)
            _, fake_features = discriminator(gen_imgs)

            g_loss_adv = adversarial_loss(validity.squeeze(), valid.squeeze())  # Adversarial loss for the generator
            g_loss_fm = feature_matching_loss(fake_features.mean(0), real_features.mean(
                0))  # Feature matching loss to make generated images similar to real images
            g_loss = g_loss_adv + g_loss_fm  # Combined loss for the generator.
            g_loss.backward()  # Backpropagates the loss(update the weights)
            optimizer_G.step()  # Updates the generator's parameters

            # Train Discriminator
            # zero the gradients of the discriminator's optimizer
            optimizer_D.zero_grad()
            real_validity, _ = discriminator(real_imgs)
            real_loss = adversarial_loss(real_validity.view(-1, 1), valid)
            fake_validity, _ = discriminator(gen_imgs.detach())
            fake_loss = adversarial_loss(fake_validity.view(-1, 1), fake)
            d_loss = (real_loss + fake_loss) / 2
            d_loss.backward()
            optimizer_D.step()

            print(
                f"[Epoch {epoch}/{n_epochs}] [Batch {i}/{len(dataloader)}] [D loss: {d_loss.item()}] [G loss: {g_loss.item()}]")

        if epoch % sample_interval == 0:
            print("Saving Ape Yacht image")
            save_image(gen_imgs.data[0], f"BoredApes/images/{epoch}.png", normalize=True)
            torch.save(generator.state_dict(), "BoredApes/models/generator_new_model.pth")
            torch.save(discriminator.state_dict(), "BoredApes/models/discriminator_new_model.pth")


if __name__ == '__main__':
    train_gan()
