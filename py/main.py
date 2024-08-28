from roku import Roku


def doStuffWithRoku(roku: Roku):
    roku.volume_down()
    print(roku.apps)
    print(roku.active_app)
    print(roku.current_app)
    print(roku.device_info)
    print(roku.power_state)
    print(roku.tv_channels)
    print(roku.commands)
    roku.volume_up()


def main():
    r = Roku.discover()
    while len(r) == 0:
        r = Roku.discover()

    roku = r[0]
    doStuffWithRoku(roku)


main()
