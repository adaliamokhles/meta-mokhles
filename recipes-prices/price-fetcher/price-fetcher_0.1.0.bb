SUMMARY = "Fetches 6 crypto prices from Coinbase and sends them on the CAN bus vcan0"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Where the code comes from: your GitHub repository, branch main, this exact commit
SRC_URI = "git://github.com/adaliamokhles/price-system.git;protocol=https;branch=main"
SRCREV = "b5f89b71ad4be768936e88147438bf3459ef4273"

# The repository contains two projects: build the one in the price-fetcher/ folder
S = "${UNPACKDIR}/${BP}/price-fetcher"

# cargo = build and install a Rust program
# cargo-update-recipe-crates = lets Yocto generate the crate list from Cargo.lock
inherit cargo cargo-update-recipe-crates

# The crate list (generated in the next step)
require ${BPN}-crates.inc
