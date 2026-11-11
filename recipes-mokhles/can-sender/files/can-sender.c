#include <stdio.h>          // printf, perror
#include <string.h>         // strcpy, memset
#include <unistd.h>         // write, close, usleep
#include <net/if.h>         // struct ifreq (interface name -> number)
#include <sys/ioctl.h>      // ioctl
#include <sys/socket.h>     // socket, bind
#include <linux/can.h>      // struct can_frame, CAN definitions
#include <linux/can/raw.h>  // CAN_RAW protocol

int main(void)
{
    int s;                      // the socket (our "phone")
    struct sockaddr_can addr;   // the CAN address to connect to
    struct ifreq ifr;           // used to look up "vcan0"
    struct can_frame frame;     // the CAN message we send

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

    // 4. Send 5 messages, every 500 ms
    for (int i = 0; i < 5; i++) {
        memset(&frame, 0, sizeof(frame));
        frame.can_id  = 0x123;   // message ID
        frame.len     = 4;       // 4 data bytes
        frame.data[0] = i;       // counter: 0,1,2,3,4
        frame.data[1] = 0xAB;
        frame.data[2] = 0xCD;
        frame.data[3] = 0xEF;

        if (write(s, &frame, sizeof(frame)) != sizeof(frame)) {
            perror("write");
            return 1;
        }
        printf("Sent message %d: ID 0x123 data %02X AB CD EF\n", i, i);
        usleep(500000);          // wait 500 ms (500,000 microseconds)
    }

    close(s);   // hang up
    return 0;
}
