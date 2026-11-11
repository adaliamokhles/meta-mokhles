SUMMARY = "Mokhles image: minimal Linux with CAN support and my own programs"
LICENSE = "MIT"

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-mokhles-can \
    hello-c \
    hello-file \
    vim \
    nano \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

IMAGE_FEATURES += "allow-empty-password empty-root-password allow-root-login"
IMAGE_LINGUAS = " "

inherit core-image
