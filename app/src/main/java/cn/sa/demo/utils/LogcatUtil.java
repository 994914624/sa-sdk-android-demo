package cn.sa.demo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by yzk on 2018/8/17
 * <p>
 * 每次冷启动时，开始抓日志（抓日志之前，会删除之前的日志文件），默认 App 进入后台时，停止抓日志。
 * 如果有 sdcard 读写权限，日志存储在 /sdcard/MyLog/ 目录下，否则存储在 /包名/files/MyLog/ 目录下。
 *
 * 使用：
 *  AndroidManifest 中添加 "android.permission.WRITE_EXTERNAL_STORAGE"
 *  在 Application 的 onCreate 中，调用 LogcatUtil.getInstance(this).init();
 *
 */

public class LogcatUtil {

    private static final String TAG = "LogcatUtil";
    private Context mContext = null;
    private boolean isStopLog= true;
    private static String LOG_PATH_MEMORY_DIR;     //日志文件在安装目录中的路径
    private static String LOG_PATH_SDCARD_DIR;     //日志文件在sdcard中的路径
    private static String FILE_SUFFIX = ".txt";    //日志文件格式
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());//日志名称格式

    private LogcatUtil() {

    }

    private LogcatUtil(Context contex) {
        this.mContext = contex;
    }

    /**
     * 单例
     */
    private static final Map<Context, LogcatUtil> mInstanceMap = new HashMap<>();
    public static LogcatUtil getInstance(Context context) {
        synchronized (mInstanceMap) {
            if (context != null) {
                Context appContext = context.getApplicationContext();
                LogcatUtil instance = mInstanceMap.get(appContext);
                if (instance == null) {
                    instance = new LogcatUtil(appContext);
                    mInstanceMap.put(appContext, instance);
                }
                return instance;
            }
            return new LogcatUtil();
        }
    }


    /**
     * init
     */
    public void init() {
        if (mContext != null) {
            if (isDebugMode(mContext) && isMainProcess(mContext)) {
                String logDir = "MyLog";
                LOG_PATH_MEMORY_DIR = mContext.getFilesDir().getAbsolutePath() + File.separator + logDir;
                LOG_PATH_SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + logDir;
                // 开始收集日志
                startCatchLog();
                //用于判断 启动、退出
                registerActivityLifeCallback();
            }
        }
    }

    /**
     * registerActivityLifecycleCallbacks
     */
    private void registerActivityLifeCallback() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                final Application app = (Application) mContext.getApplicationContext();
                app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

                    private Integer startedActivityCount = 0;
                    private final Object mActivityLifecycleCallbacksLock = new Object();

                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        requestPermission(activity);
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {

                        synchronized (mActivityLifecycleCallbacksLock) {
                            if (startedActivityCount == 0) {
                                // App 启动
                                log( "App 启动");
                            }
                            startedActivityCount = startedActivityCount + 1;
                        }
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {

                    }

                    @Override
                    public void onActivityPaused(Activity activity) {

                    }

                    @Override
                    public void onActivityStopped(Activity activity) {

                        synchronized (mActivityLifecycleCallbacksLock) {
                            startedActivityCount = startedActivityCount - 1;
                            if (startedActivityCount == 0) {
                                // App 启动 时，停止抓日志
                                log("App 退出");
                                stopCatchLog();
                            }
                        }
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {

                    }
                });
            }

    }

    /**
     * request sdcard runtime permission
     */
    @TargetApi(23)
    private void requestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 1010);
                log("request sdcard permission");
            }
        }
    }

    /**
     * 开始 catch log
     */
    private Process process;
    private void startCatchLog() {
        log("startCatchLog");
        logcat_c();// 先清除 logcat 缓存
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteLogFile(); // 删除之前的日志文件
                logcat_v_time();// logcat -v time 收集日志信息
            }
        }).start();
    }

    /**
     * cmd logcat -c
     */
    private void logcat_c() {
        log("logcat_c");
        // logcat -c
        List<String> commandC = new ArrayList<>();
        commandC.add("logcat");
        commandC.add("-c");
        try {
            Runtime.getRuntime().exec(commandC.toArray(new String[commandC.size()])).waitFor();;
        } catch (Exception e) {
            e.getMessage();

        }
    }

    /**
     * cmd logcat -v time -f xxxx.txt
     */
    private void logcat_v_time() {
        if(mContext!=null) {
            log("logcat_v_time");
            //logcat -v time
            List<String> commandList = new ArrayList<String>();
            commandList.add("logcat");
            commandList.add("-v");
            commandList.add("time");
            //commandList.add("*:I");
            commandList.add("-f");
            String filePath;
            if (checkPermission()) {
                filePath = getMemLogPath();
                commandList.add(filePath); // 6.0 以上，没有 sdcard 权限时，先输出到 getFilesDir
                log("getFilesDir---" + filePath);
            } else {
                filePath = getSdcardLogPath();
                commandList.add(filePath);// sdcard
                log( "sdcard---" + filePath);
            }
            log("commandList:"+commandList.toString());
            // 过滤指定TAG的信息
            // commandList.add("LogcatUtil:V");
            // commandList.add("*:S");
            try {
                process = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * 关闭 App 内开启的 logcat 进程
     * 根据用户名称杀死进程(如果是本程序进程开启的 logcat 收集进程那么两者的 USER 一致)
     * 如果不关闭，可能会有多个进程读取 logcat 日志缓存信息写入日志文件
     */
    private void killLogcatProcss(Context context) {
        if(context!=null) {
            List<String> orgProcessList = getAllProcess();
            List<ProcessInfo> allProcList = getProcessInfoList(orgProcessList);
            if (process != null) {
                process.destroy();
            }
            String packName = context.getPackageName();
            String myUser = getAppUser(packName, allProcList);
            for (ProcessInfo processInfo : allProcList) {
                if (processInfo.name.toLowerCase().equals("logcat") && processInfo.user.equals(myUser)) {
                    android.os.Process.killProcess(Integer.parseInt(processInfo.pid));
                    log( "--kill logcat process-- :" + processInfo.pid);
                }
            }
        }
    }

    /**
     * 根据 ps 命令得到的内容获取 PID，User，name 等信息
     */
    private List<ProcessInfo> getProcessInfoList(List<String> orgProcessList) {
        List<ProcessInfo> procInfoList = new ArrayList<ProcessInfo>();
        for (int i = 1; i < orgProcessList.size(); i++) {
            String processInfo = orgProcessList.get(i);
            String[] proStr = processInfo.split(" ");
            // USER PID PPID VSIZE RSS WCHAN PC NAME
            // root 1 0 416 300 c00d4b28 0000cd5c S /init
            List<String> orgInfo = new ArrayList<String>();
            for (String str : proStr) {
                if (!"".equals(str)) {
                    orgInfo.add(str);
                }
            }
            if (orgInfo.size() == 9) {
                ProcessInfo pInfo = new ProcessInfo();
                pInfo.user = orgInfo.get(0);
                pInfo.pid = orgInfo.get(1);
                pInfo.ppid = orgInfo.get(2);
                pInfo.name = orgInfo.get(8);
                procInfoList.add(pInfo);

                log("pInfo" + pInfo.toString());
            }
        }
        return procInfoList;
    }

    /**
     * 获取 App 的用户名称
     */
    private String getAppUser(String packName, List<ProcessInfo> allProcList) {
        for (ProcessInfo processInfo : allProcList) {
            if (processInfo.name.equals(packName)) {
                return processInfo.user;
            }
        }
        return null;
    }

    /**
     * 运行 PS 命令得到进程信息
     *
     * @return USER PID PPID VSIZE RSS WCHAN PC NAME
     * root 1 0 416 300 c00d4b28 0000cd5c S /init
     */
    private List<String> getAllProcess() {
        List<String> orgProcList = new ArrayList<>();
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec("ps");

            InputStreamReader isr = new InputStreamReader(proc
                    .getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                orgProcList.add(line);
            }

            if (proc.waitFor() != 0) {
                Log.e(TAG, "getAllProcess proc.waitFor() != 0");
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllProcess failed", e);
        } finally {
            try {
                if (proc != null) proc.destroy();
            } catch (Exception e) {
                Log.e(TAG, "getAllProcess failed", e);
            }
        }
        return orgProcList;
    }

    /**
     * 保存进程 info 类
     */
    class ProcessInfo {
        private String user;
        private String pid;
        private String ppid;
        public String name;

        @Override
        public String toString() {
            String str = "user=" + user + " pid=" + pid + " ppid=" + ppid
                    + " name=" + name;
            return str;
        }
    }


    /**
     * 尝试将 getFileDir 目录下的日志文件转移到 SD 卡下面
     */
    public void moveLogfile() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        File file = new File(LOG_PATH_SDCARD_DIR);
        if (!file.isDirectory()) {
            boolean mkOk = file.mkdirs();
            if (!mkOk) {
                return;
            }
        }

        if(checkPermission()){
            return;
        }

        file = new File(LOG_PATH_MEMORY_DIR);
        if (file.isDirectory()) {
            File[] allFiles = file.listFiles();
            for (File logFile : allFiles) {
                String fileName = logFile.getName();
                // copy 文件
                boolean isSucc = copy(logFile, new File(LOG_PATH_SDCARD_DIR
                        + File.separator + fileName));
                // 删除 getFileDir 产生的日志文件
                if (logFile.delete()) {
                    log("delete getFileDir log file success");
                }

            }
        }
    }

    /**
     * android.permission.WRITE_EXTERNAL_STORAGE
     */
    private boolean checkPermission() {
         return mContext!=null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(mContext, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 拷贝文件
     *
     * @param source from
     * @param target to
     * @return isSucc
     */
    private boolean copy(File source, File target) {
        boolean isSucc = false;
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            if (!target.exists()) {
                boolean createSucc = target.createNewFile();
                if (!createSucc) {
                    return false;
                }
            }
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            byte[] buffer = new byte[8 * 1024];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            isSucc = true;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "copy file fail");
            isSucc = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "finally copy file fail");
                isSucc = false;
            }
        }
        return isSucc;
    }

    /**
     * 是否是 main process
     */
    private boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        if (am == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                log("main process::" + info.processName+"："+info.pid);
                return true;
            }
        }
        return false;
    }

    /**
     * sdcard 日志文件 path
     */
    private String getSdcardLogPath() {
        createLogDir();
        String logFileName = sdf.format(new Date()) + FILE_SUFFIX;// 日志文件名称
        return LOG_PATH_SDCARD_DIR + File.separator + logFileName;

    }

    /**
     * getFilesDir 日志文件 path
     */
    private String getMemLogPath() {
        createLogDir();
        String logFileName = sdf.format(new Date()) + FILE_SUFFIX;// 日志文件名称
        return LOG_PATH_MEMORY_DIR + File.separator + logFileName;

    }

    /**
     * 删除日志文件
     */
    private void deleteLogFile() {
        //delete sdcard log file
        try {
            File file = new File(LOG_PATH_SDCARD_DIR);
            if (file.isDirectory()) {
                File[] allFiles = file.listFiles();
                if (allFiles != null) {
                    for (File logFile : allFiles) {
                        if (logFile.delete()) {
                            log( "delete log file success,path:" + logFile.getAbsolutePath());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("delete sdcard log file error");
        }
        //delete getFileDir log file
        try {
            File file = new File(LOG_PATH_MEMORY_DIR);
            if (file.isDirectory()) {
                File[] allFiles = file.listFiles();
                if (allFiles != null) {
                    for (File logFile : allFiles) {
                        if (logFile.delete()) {
                            log( "delete log file success,path:" + logFile.getAbsolutePath());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("delete  getFileDir log file error");
        }
    }

    /**
     * mk dir
     */
    private void createLogDir() {
        File file = new File(LOG_PATH_MEMORY_DIR);
        boolean mkOk;
        if (!file.isDirectory()) {
            mkOk = file.mkdirs();
            if (!mkOk) {
                file.mkdirs();
            }
        }
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            file = new File(LOG_PATH_SDCARD_DIR);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
        }
    }

    /**
     * @param context App 的 Context
     * @return debug return true,release return false
     * 用于判断是 debug 包，还是 relase 包
     */
    private   boolean isDebugMode(Context context) {
        if(context!=null) {
            try {
                ApplicationInfo info = context.getApplicationInfo();
                return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     *
     */
    private void log(String str){
        if(isDebugMode(mContext)){
            Log.d(TAG,str);
        }
    }

    /**
     * isStopLog 默认为 true
     */
    public void setStopLog(boolean stopLog){
        this.isStopLog=stopLog;
    }

    /**
     * 停止 catch 日志
     */
    public void stopCatchLog() {
        if( isStopLog) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        killLogcatProcss(mContext);// 杀掉 logcat process，停止收集日志。
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 如果日志写在 getFileDir 目录 ，尝试 copy 到 sdcard
                    moveLogfile();
                }
            }).start();

        }
    }

}
