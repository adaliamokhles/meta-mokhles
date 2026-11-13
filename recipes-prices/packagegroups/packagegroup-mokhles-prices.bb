SUMMARY = "Mokhles price system: fetcher, dashboard, web charts, autostart and CAN socket support"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS:${PN} = "\
    price-fetcher \
    price-dashboard \
    price-web \
    price-system-init \
    kernel-module-can-raw \
"
