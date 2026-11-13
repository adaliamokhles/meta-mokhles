SUMMARY = "Listens to the 6 crypto prices on CAN (vcan0) and serves a web page with live charts on port 8080"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Same repository and commit as price-fetcher and price-dashboard
SRC_URI = "git://github.com/adaliamokhles/price-system.git;protocol=https;branch=main"
SRCREV = "901fcc00b6d969b2187fa5aedc025ce074a7537a"

# This recipe builds the project in the price-web/ folder
S = "${UNPACKDIR}/${BP}/price-web"

inherit cargo cargo-update-recipe-crates

require ${BPN}-crates.inc
