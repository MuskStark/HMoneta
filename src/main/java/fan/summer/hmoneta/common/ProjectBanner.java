package fan.summer.hmoneta.common;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

/**
 * Banner
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/19
 */
public class ProjectBanner implements Banner {
    private static final String BANNER =
            " _    _  __  __                           _        \n" +
                    "| |  | ||  \\/  |                         | |       \n" +
                    "| |__| || \\  / | ___  _ __ ___   ___  ___| |_ __ _ \n" +
                    "|  __  || |\\/| |/ _ \\| '_ ` _ \\ / _ \\/ __| __/ _` |\n" +
                    "| |  | || |  | | (_) | | | | | |  __/ (__| || (_| |\n" +
                    "|_|  |_||_|  |_|\\___/|_| |_| |_|\\___|\\___|\\__\\__,_|\n" +
                    "                                                  \n" +
                    "                    HMoneta (ver)               \n";
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        String version = environment.getProperty("hmoneta.version");
        assert version != null;
        String banner = BANNER.replace("ver", version);
        out.print(banner);
    }
}
