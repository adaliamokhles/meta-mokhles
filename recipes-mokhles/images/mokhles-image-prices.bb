SUMMARY = "Crypto price system: prices from the internet, sent over CAN, shown on a dashboard and live web charts"
LICENSE = "MIT"

IMAGE_INSTALL = "\
    packagegroup-core-boot \
    can-setup \
    dns-setup \
    can-utils \
    packagegroup-mokhles-prices \
    nano \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

IMAGE_FEATURES += "allow-empty-password empty-root-password allow-root-login"
IMAGE_LINGUAS = " "

inherit core-image

# Port forwarding for runqemu (slirp): Ubuntu port 8080 -> device port 8080 (price-web)
QB_SLIRP_OPT = "-netdev user,id=net0,hostfwd=tcp::8080-:8080"
