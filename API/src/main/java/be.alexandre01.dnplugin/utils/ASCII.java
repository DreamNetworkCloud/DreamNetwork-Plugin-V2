package be.alexandre01.dnplugin.utils;

import be.alexandre01.dnplugin.utils.colors.Colors;

public class ASCII {
    /*public static void sendDNText(){
        System.out.print("\n______                               _   _        _                          _    \n" +
                "|  _  \\                             | \\ | |      | |                        | |   \n" +
                "| | | | _ __  ___   __ _  _ __ ___  |  \\| |  ___ | |_ __      __ ___   _ __ | | __\n" +
                "| | | || '__|/ _ \\ / _` || '_ ` _ \\ | . ` | / _ \\| __|\\ \\ /\\ / // _ \\ | '__|| |/ /\n" +
                "| |/ / | |  |  __/| (_| || | | | | || |\\  ||  __/| |_  \\ V  V /| (_) || |   |   < \n" +
                "|___/  |_|   \\___| \\__,_||_| |_| |_|\\_| \\_/ \\___| \\__|  \\_/\\_/  \\___/ |_|   |_|\\_\\\n" +
                "                                                                                  \n" +
                "     " +
                "                                                                             ");
    }*/
    public static void sendDNText(){
        System.out.println(Colors.CYAN+
                "______                               _   _        _                          _    \n" +
                "|  _  \\                             | \\ | |      | |                        | |   \n" +
                "| | | | _ __  ___   __ _  _ __ ___  |  \\| |  ___ | |_ __      __ ___   _ __ | | __\n" +
                "| | | || '__|/ _ \\ / _` || '_ ` _ \\ | . ` | "+Colors.CYAN_BRIGHT+"/ _ \\| __|\\ \\ /\\ / // _ \\ | '__|| |/ /\n" +
                "| |/ / | |  |  __/| (_| || | | | | || |\\  ||  __/| |_  \\ V"+Colors.PURPLE_BOLD_BRIGHT+"  V /| (_) || |   |   <     "+Colors.WHITE_BOLD_UNDERLINED+"2023"+Colors.RESET+Colors.PURPLE_BOLD_BRIGHT+"\n" +
                "|___/  |_|   \\___| \\__,_||_| |_| |_|\\_| \\_/ \\___| \\__|  \\_/\\_/  \\___/ |_|   |_|\\_\\      "+Colors.YELLOW_BRIGHT_UNDERLINE +"(BETA V2 - 1.11)\n" +Colors.RESET);
    }

}
