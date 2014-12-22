package util;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import scala.reflect.io.Path;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 22.12.2014
 * Time: 21:44
 */
public class Provisioner extends GlobalSettings {
    private static final String[] PROVISION_COMMAND = new String[]{
            "/bin/bash",
            "/usr/bin/provision_nodes"
    };

    private static final String LOG_FOLDER = "provision-logs";

    // check the flag every 5000ms
    private static final Integer INTERVAL = 100;

    // after this many intervals we will definetly run ansible
    private static final Integer INTERVAL_LIMIT = 60;

    // the number of nodes that have to be active
    private static final Integer ACTIVE_NODE_COUNT = 6;

    private static AtomicInteger intervalCount = new AtomicInteger(0);
    private static AtomicBoolean isRunning = new AtomicBoolean(false);

    @Override
    public void onStart(Application app) {
        startProvisionWatcher();
    }

    public static List<String> getLogFiles() {
        ArrayList<String> retList = new ArrayList<String>();

        File folder = new File(LOG_FOLDER);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.getName().startsWith("provision-") && file.getName().endsWith(".log")) {
                retList.add(file.getName());
            }
        }

        return retList;
    }

    public static String getContentOfNthLogFile(Integer n) {
        List<String> logFiles = getLogFiles();

        if (logFiles.size() >= n) {
            try {

                File file = new File(logFiles.get(n));
                byte[] encoded = Files.readAllBytes(Paths.get(LOG_FOLDER + "/" + file.getName()));
                return new String(encoded, Charset.defaultCharset());

            } catch (IOException e) {
                Logger.error("error reading from log files");
                e.printStackTrace();
                return "";
            }

        } else {
            return "";
        }
    }

    public static void startProvisionWatcher() {
        Logger.info("Starting the provisioning service...");

        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (!isRunning.getAndSet(true)) {

                    if (NodeStorage.getActiveNodes().size() < ACTIVE_NODE_COUNT) {
                        Logger.info("Not enough active nodes, need to reprovision");
                        executeAnsible();

                    } else if (INTERVAL_LIMIT == intervalCount.get()) {
                        Logger.info("Interval limit reached => running ansible");
                        executeAnsible();
                    }

                    intervalCount.incrementAndGet();
                    isRunning.set(false);
                }
            }
        }, INTERVAL, INTERVAL);
    }

    private static void executeAnsible() {
        try {
            Runtime rt = Runtime.getRuntime();
            // for testing something like this
            // Process ps = rt.exec("C:\\Users\\Thomas\\workspace\\onion-routing\\directory\\provision_nodes.bat");
            Process ps = rt.exec(PROVISION_COMMAND);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            File file = new File("provision-logs/provision-" + dateFormat.format(new Date()) + ".log");
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            pipe(ps.getInputStream(), fileOutputStream);
            fileOutputStream.close();

        } catch (IOException e) {
            Logger.error("Error running external service - probably file not found");
        }
    }

    private static void pipe(InputStream is, OutputStream os) throws IOException {
        int n;
        byte[] buffer = new byte[1024];
        while ((n = is.read(buffer)) > -1) {
            os.write(buffer, 0, n);
        }
        os.close();
    }
}
