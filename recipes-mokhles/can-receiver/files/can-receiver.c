#include <stdio.h>          // printf, perror
#include <string.h>         // strcpy, memset
#include <unistd.h>         // read, close
#include <net/if.h>         // struct ifreq
#include <sys/ioctl.h>      // ioctl
#include <sys/socket.h>     // socket, bind
#include <linux/can.h>      // struct can_frame
#include <linux/can/raw.h>  // CAN_RAW

int main(void)
{
    int s;
    struct sockaddr_can addr;
    struct ifreq ifr;
    struct can_frame frame;

    // 1. Ask the kernel for a raw CAN socket
    s = socket(PF_CAN, SOCK_RAW, CAN_RAW);
    if (s < 0) { perror("socket"); return 1; }

    // 2. Find the interface number of "vcan0"
    strcpy(ifr.ifr_name, "vcan0");
    if (ioctl(s, SIOCGIFINDEX, &ifr) < 0) { perror("ioctl vcan0"); return 1; }

    // 3. Connect (bind) the socket to vcan0
    memset(&addr, 0, sizeof(addr));
    addr.can_family  = AF_CAN;
    addr.can_ifindex = ifr.ifr_ifindex;
    if (bind(s, (struct sockaddr *)&addr, sizeof(addr)) < 0) { perror("bind"); return 1; }

    printf("Receiver ready, waiting for 5 messages on vcan0...\n");

    // 4. Receive and interpret 5 messages
    for (int i = 0; i < 5; i++) {
        if (read(s, &frame, sizeof(frame)) < 0) {   // waits here until a message arrives
            perror("read");
            return 1;
        }

        if (frame.can_id == 0x123) {
            printf("Received ID 0x123 -> counter = %d\n", frame.data[0]);
        } else {
            printf("Received unknown ID 0x%X, data:", frame.can_id);
            for (int j = 0; j < frame.len; j++) {
                printf(" %02X", frame.data[j]);
            }
            printf("\n");
        }
    }

    printf("Receiver done.\n");
    close(s);
    return 0;
}
