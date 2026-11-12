SUMMARY = "Mokhles price system: crypto price fetcher, CAN dashboard and CAN socket support"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = "\
    price-fetcher \
    price-dashboard \
    kernel-module-can-raw \
"
