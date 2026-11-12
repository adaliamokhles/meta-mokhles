SUMMARY = "Receives 6 crypto prices from the CAN bus vcan0 and shows a live table"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Same repository and commit as price-fetcher
SRC_URI = "git://github.com/adaliamokhles/price-system.git;protocol=https;branch=main"
SRCREV = "1f08725e84967321f581d9129c1f9f9ed12a244c"

# This recipe builds the project in the price-dashboard/ folder
S = "${UNPACKDIR}/${BP}/price-dashboard"

inherit cargo cargo-update-recipe-crates

require ${BPN}-crates.inc
