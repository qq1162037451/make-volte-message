package com.jhhc.work;

import com.jhhc.conf.DataSourceService;
import com.jhhc.conf.LogService;
import com.jhhc.conf.Setting;
import com.jhhc.conf.Setting.*;
import com.jhhc.utils.DateUtils;
import com.jhhc.utils.IOUtils;
import com.jhhc.utils.RandomFault;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.embedded.DataSourceFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.swing.text.DateFormatter;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jhhc.conf.Setting.*;

/**
 * @author xiaojiang
 * @date 2021/1/7 17:01
 */
@Component
public class MakeData {

    @Autowired
    private Setting setting;
    
    @Autowired
    private LogService log;

    @Autowired
    private DataSourceService dataSourceService;

    private int fileReq = 0;

    private List<String> rightMessage = new ArrayList<>(50);

    private List<String> errorMessage = new ArrayList<>(282);

    private String[] proves = {"100","200","791"};

    private Map<String,List<String>> provesMap = new HashMap<>(3);


    public MakeData() {
        {
            System.out.println(("初始化数据开始..."));
            /* 初始化省-号段数据 数据来源于 select a.msisdn_area_id msisdn, b.hm_prov_cd provCd
                from mcbdba.imsi_ld_cd a, mcbdba.ld_area_cd_prov b
                where a.ld_area_cd = b.idd_area_cd and (sysdate between a.effc_tm and a.expired_tm) and (sysdate between b.effc_tm and b.expired_tm)
                and b.HM_PROV_CD in ('100','200','791') */
            List<String> msisdn100 = new ArrayList<>(120);
            List<String> msisdn200 = new ArrayList<>();
            List<String> msisdn791 = new ArrayList<>();
            for (int i = 1340100; i < 1340120; i++) { //20
                msisdn100.add(String.valueOf(i));
            }
            for (int i = 1342600; i < 1342650; i++) { // 70
                msisdn100.add(String.valueOf(i));
            }
            for (int i = 1346630; i < 1346680; i++) { // 120
                msisdn100.add(String.valueOf(i));
            }
            for (int i = 1348860; i < 1348890; i++) { // 150
                msisdn100.add(String.valueOf(i));
            }
            provesMap.put("100", msisdn100); // 100省号段加载完毕
            for (int i = 1358030; i < 1358060; i++) { // 30
                msisdn200.add(String.valueOf(i));
            }
            for (int i = 1359070; i < 1359100; i++) { // 60
                msisdn200.add(String.valueOf(i));
            }
            for (int i = 1343020; i < 1343040; i++) { // 80
                msisdn200.add(String.valueOf(i));
            }
            for (int i = 1348010; i < 1348030; i++) { // 100
                msisdn200.add(String.valueOf(i));
            }
            provesMap.put("200", msisdn200); // 200省号段加载完毕
            for (int i = 1347910; i < 1347940; i++) { // 30
                msisdn791.add(String.valueOf(i));
            }
            for (int i = 1347950; i < 1347990; i++) { // 70
                msisdn791.add(String.valueOf(i));
            }
            for (int i = 1387910; i < 1387960; i++) { // 120
                msisdn791.add(String.valueOf(i));
            }
            provesMap.put("791", msisdn791); // 791省号段加载完毕

            InputStream in = null;
            Reader reader = null;
            BufferedReader br = null;
            try {
                // 加载正确的格式
                in = this.getClass().getClassLoader().getResourceAsStream("rightMessageFormat.txt");
                reader = new InputStreamReader(in);
                br = new BufferedReader(reader);
                String line;
                while ((line = br.readLine()) != null) {
                    rightMessage.add(line);
                }

                // 加载错误的格式
                in = this.getClass().getClassLoader().getResourceAsStream("errorMessageFormat.txt");
                reader = new InputStreamReader(in);
                br = new BufferedReader(reader);
                while ((line = br.readLine()) != null) {
                    errorMessage.add(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.warn("加载模板文件失败...");
            } finally {
                IOUtils.closeQuietly(br, reader, in);
                System.out.println("初始化数据结束...");
            }
        }

    }

    public void makeData(int fileCount, int dataCount, String date) throws Exception {

        String fileOutPath = setting.getFileOutPath();
        log.info("生成文件存放目录为：" + fileOutPath);
        File path = new File(fileOutPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        long start = System.currentTimeMillis();
        Long startTm = DateUtils.parseYYYYMMDD(date).getTime();
        getSeq(date); // 初始化seq
        for (int i = 0; i < fileCount; i++) {
            Thread.sleep(setting.getCreateThreadSleep());
            int finalI = i;
            String provCd = proves[finalI % proves.length];
            new Thread(() -> {
                final FileWriter[] fw = {null};
                final BufferedWriter[] bw = {null};
                final PrintWriter[] pw = {null};
                log.info("线程：" + Thread.currentThread().getName() + "开始运行...");
                String fileName = path + "/" +  getFileName(date, provCd);
                log.info("本次生成文件名: " + fileName);
                try {
                    fw[0] = new FileWriter(fileName);
                    bw[0] = new BufferedWriter(fw[0]);
                    pw[0] = new PrintWriter(bw[0]);

                    log.info("开始写入头数据");
                    String seq = fileName.substring(fileName.length() - 9, fileName.length() - 4);
                    writeHead(pw[0], seq);
                    log.info("开始逐行写入数据...");
                    for (int j = 0; j < dataCount; j++) {
                        String s = "";
                        if (RandomFault.getFault(setting.getErrorRate())) {
                            s = getErrorMessage(j);
                        } else {
                            s = getLineNew(j * (finalI+1), provCd, startTm);
                        }

                        pw[0].println(s);
                    }
                    log.info("开始写入尾数据");
                    writeTail(pw[0], seq, dataCount);
                    pw[0].flush();
                    long end = System.currentTimeMillis();
                    log.info("文件生成成功, 耗时:" + (end-start) + "毫秒, 开始重命名...");
                    pw[0].close(); // 把流关掉才能修改名称
                    bw[0].close();
                    fw[0].close();
                    File file = new File(fileName);
                    Thread.sleep(setting.getWaitIoCloseSleep());
                    // 修改为正式名称
                    String newFileName = fileName.replace(".tmp", "").replace("\\", "/");
                    File finalFile = new File(newFileName);
                    boolean result = file.renameTo(finalFile);
                    log.info("修改名称结果: " + result);
                    log.info("开始写入文件场景对照表：io_subchg_file_create_log...");
                    writeLog(newFileName,dataCount,"0000");
                    log.info("线程：" + Thread.currentThread().getName() + "正常运行结束，共耗时:" + (System.currentTimeMillis() - start) + "毫秒");
                } catch (IOException e) {
                    log.warn("生成日志时出现异常");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(pw[0], bw[0], fw[0]);
                }
            }).start();
        }
    }

    private void writeLog(String fileName, int dataCount, String sceneCode) {
        try {
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            String sql = "insert into io_subchg_file_create_log(file_name, data_count, scene_code, create_tm) values (?,?,?,?)";
            int result = dataSourceService.getJdbcTemplate().update(sql, fileName, dataCount, sceneCode, new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getSeq(String date) {
        try {
            String sql = "select max(substr(file_name,16)) from io_subchg_file_create_log a where to_char(a.create_tm,'yyyyMMdd') = " + "'" + date +"'";
            System.out.println(sql);
            int result = dataSourceService.getJdbcTemplate().queryForInt(sql);
            fileReq = result * proves.length + proves.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 文件名格式： IVLTAAAYYYYMMDDNNNNN
    private synchronized String getFileName(String date, String provCode) { //  长计算AtomicInteger派不上用场
        StringBuffer fileName = new StringBuffer();
        fileName.append("IVLT");
        fileName.append(provCode).append(date).append(getSeq(fileReq / proves.length, false)).append(".tmp");
        fileReq++;
        return fileName.toString();
    }

    private String getSeq(int index, boolean msisdnFlag) {
        String NNNNN = null;
        if (index < 10) {
            NNNNN = "0000" + index;
        } else if (index >= 10 && index < 100) {
            NNNNN = "000" + index;
        } else if (index >= 100 && index < 1000) {
            NNNNN = "00" + index;
        } else if (index >= 1000 && index < 10000) {
            NNNNN = "0" + index;
        } else if (index > 99999) {
            log.error("文件数量过多，程序退出...");
            System.exit(1);
        }
        return msisdnFlag ? NNNNN.substring(1) : NNNNN;
    }

    /**
     * 生成头记录
     * @param pw
     * @param seq 文件序号
     */
    private void writeHead(PrintWriter pw, String seq) {
        String head = "10" + seq + "GMBOSS" + DateUtils.formatYYYYMMDDYYYYMMDD(new Date());
        pw.println(head);
    }

    /**
     * 生成尾记录
     * @param pw
     * @param fileName
     * @param dataCount
     */
    private void writeTail(PrintWriter pw, String seq, int dataCount) {
        String head = "90" + seq + "GMBOSS" + dataCount;
        pw.println(head);
    }

    /**
     * 生成各行数据
     * 老方法，废弃
     * @return
     */
    @Deprecated
    private String getLineOld() {
        StringBuffer sb = new StringBuffer();
        sb.append("69").append("|"); // 1、话单类型：必须为69
        sb.append("C-2").append("|"); // 2、未知含义,不校验
        sb.append("BYE-3").append("|"); // 3、未知含义,不校验
        sb.append("0").append("|"); // 4、用于区分话单类型，取值(0-主叫话单,1-被叫话单,4-前转话单)
        sb.append("192.168.10.2").append("|"); // 5、网元标识,可以是IP也可以是域名 非空
        sb.append("z9hG4bK1klmpxylz14k0mk43e2xz9e1390pa41ae@139.110.168.35").append("|"); // 6、Call-ID 非空
        sb.append("008613774548679").append("|"); // *7、主叫方标识，当4为0时校验：11位数字、校验省代码；当4为1或4时，取值：0-9、*、#、+；
        sb.append("M-8").append("|"); // 8、标识 非空
        sb.append("00115025676").append("|"); // *9、被叫方标识，当4为0或4时校验：0-9，*，#，+；当4为1时校验：11位数字、校验省代码
        sb.append("20201223081327").append("|"); // 10、服务请求的时间 非空、时间格式：yyyyMMddHHmmss
        sb.append("20201223081517").append("|"); // 11、服务开始时间 时间格式：yyyyMMddHHmmss、*不可早于话单有限期限7天、不可晚于当前时间+5分钟
        sb.append("20201223082027").append("|"); // 12、服务结束时间 不校验
        sb.append("20201222082127").append("|"); // 13、创建话单的时间 非空 时间格式：yyyyMMddHHmmss 与14一致
        sb.append("20201222082127").append("|"); // 14、关闭话单的时间 非空 时间格式：yyyyMMddHHmmss 与13一致
        sb.append("M-15").append("|"); // 15、呼叫目的方的归属网络标识  不校验
        sb.append("M-16").append("|"); // 16、呼叫发起方的网络标识 不校验
        sb.append("M-17").append("|"); // 17、呼叫目的方的网络标识 不校验
        sb.append("1").append("|"); // *18、唯一的数字 大于等于1
        sb.append("1").append("|"); // 19、生成部分话单的情况下该字段必选 如有此字段，必须为数字且大于等于1
        sb.append("0").append("|"); // 20、话单关闭原因：0-正常关闭 1-通话不成功或者收到异常结束响应 3-未收到AS发送的ACR消息 4-发生音视频切换 5-发生超长通话话单切割 6-未处理生成部分CDR的ACR消息数达到预先设定的阈值
        sb.append("ABD98D440AA3420160904181326").append("|"); // 21、计费标识 非空
        sb.append("0").append("|"); // 22、取值0或1 0-音频业务 1-视频业务
        sb.append("M-23").append("|"); // 23、媒体类型 不校验
        sb.append("O-24").append("|"); // 24、用来描述一个会话中SDP的数据信息 不校验
        sb.append("M-25").append("|"); // 25、用来描述一个会话中的SDP数据是SDP请求还是SDP应答 不校验
        sb.append("M-26").append("|"); // 26、用来表示媒体切换的请求时间 不校验
        sb.append("2000K").append("|"); // 27、用来表示媒体切换的完成时间 不校验
        sb.append("M-28").append("|"); // 28、用来描述当前会话的媒体信息 不校验
        sb.append("M-29").append("|"); // 29、描述本次会话SDP的媒体名 不校验
        sb.append("M-30").append("|"); // 30、描述SDP-Media-Name对应的媒体属性 不校验
        sb.append("C-31").append("|"); // 31、缩写为GCID，描述承载层（分组域）的标识 不校验
        sb.append("O-32").append("|"); // 32、用来标识被叫用户发起了会话修改 不校验
        sb.append("O-33").append("|"); // 33、本字段的含义是提供媒体承载支持的GGSN的地址类型和IP地址 不校验
        sb.append("M-34").append("|"); // 34、业务请求成功或者失败的原因码 不校验
        // // *35、接入网信息 取值CS或存在sbc-domain  CS:被叫用户通过2/3G接入
        sb.append("3GPP-E-UTRAN;utran-cell-id-3gpp=460006716ccad746;network-provided;sbc-domain=sbc.001.004.zj.chinamobile.com;ue-ip=[2409:8805:8a38:844c::28e6:9861];ue-port=31923").append("|");
        sb.append("C-36").append("|"); // 36、用于携带被叫用户的2/3G位置信息 不校验
        sb.append("001").append("|"); // 37、用于携带MscNumber 若有值则必须以国际长途区号开头、不可与45同时为空
        sb.append("C-38").append("|"); // 38、用于携带位置号 不校验
        sb.append("C-39").append("|"); // 39、用于携带Cell ID 不校验
        sb.append("13774548679").append("|"); // *40、原始被叫标识 当4为0或1时不校验、当4为4时取值为11位数字且需确认省代码
        sb.append("13774548679").append("|"); // 41、计费方标识。用来填写当前计费方的IMPU 当4为0时与7一致(号码一致即可)、当4为1时与9一致、当4为4时与40一致
        sb.append("1").append("|"); // 42、在线计费标示 0-离线计费 1-在线计费
        sb.append("1000").append("|"); // 43、通话时长，单位为秒 非负全数字
        /**
         * 补充业务类型如下：
         * mMTCFU  (101),
         * mMTCFB  (103),
         * mMTCFNR  (105),
         * mMTCFNRC  (109),
         * mMTOIP  (115),
         * mMTNPTY  (118),
         * mMTHOLD  (121),
         * mMTICO  (122),
         * mMTOUTG  (123),
         * mMTECF  (125),
         * mMTBOIC  (136),
         * mMTBOIC-exHC  (137),
         * mMTBAIC-Roam  (138)
         */
        sb.append("101").append("|"); // 44、补充业务标识
        sb.append("\"hzpsbc2bhw.zj.ims.mnc410.mcc310.3gppnetwork.org\"").append("|"); // *45、用户所在所拜访的网络标识 不可与37同时为空
        sb.append("1").append("|"); // 46、标识，如有则取值 0-CS 1-LTE  与35一致，35为CS时本值为0
        sb.append("C-47").append("|"); // 47、不校验
        sb.append("C-48").append("|"); // 48、不校验
        sb.append("10000000"); // 49、本字段用于携带智能网剔单信息 如有，取值10000000 或 21000000
        return sb.toString();
    }

    private String[] column4 = {"0","0","0","0","0","1","1","1","1","4"}; // 5:4:1
    List<String> otherProvMisdnList = null;
    /**
     * 逐行写入数据
     * ##4##:话单类型           [0,1,4]
     * ##7##:主叫方标识         合法的手机号码，省代码必须与文件名中的一致
     * ##9##:被叫方标识         [0-9，*，#，+] 合法的手机号码
     * ##11##:服务开始时间      yyyyMMddHHmmss 不可早于话单有限期限7天 不可晚于当前时间+5分钟
     * ##21##:计费标识         唯一ID
     * ##40##原始被叫标识      当##4##为0和1时为空、当##4##为4时必须为合法的手机号码
     * ##41##:计费方标识       当##4##为0时与##7##一致(号码一致即可)、当##4##为1时与##9##一致、当##4##为4时与##40##一致
     *
     *
     * @param index
     * @return
     */
    private String getLineNew(int index, String provCd, Long start) {
        String line = rightMessage.get(index % rightMessage.size());
        // 话单类型
        String c4 = column4[index % column4.length];
        line = line.replaceAll("##4##", c4);
        List<String> msisdnList = provesMap.get(provCd);
        // 主叫方标识
        String c7 = msisdnList.get(cutCount(index, msisdnList.size() - 1)) + getSeq(index % 10000, true);
        line = line.replaceAll("##7##", c7);

        if (otherProvMisdnList == null) {
            if ("100".equals(provCd)) {
                otherProvMisdnList = provesMap.get("200");
                otherProvMisdnList.addAll(provesMap.get("791"));
            } else if ("200".equals(provCd)) {
                otherProvMisdnList = provesMap.get("100");
                otherProvMisdnList.addAll(provesMap.get("791"));
            } else if ("791".equals(provCd)) {
                otherProvMisdnList = provesMap.get("100");
                otherProvMisdnList.addAll(provesMap.get("200"));
            }
        }
        // 被叫方标识
        String c9 = null;
        try {
            c9 = otherProvMisdnList.get(cutCount(index, otherProvMisdnList.size() - 1)) + getSeq(index % 10000, true);
            line = line.replaceAll("##9##", c9);
        }catch (Exception e) {
            log.info(String.valueOf(index));
        }
        // 服务开始时间
        String c11 = DateUtils.formatYYYYMMDDYYYYMMDD(new Date(start + index * 10));

        line = line.replaceAll("##11##", c11);

        // 服务结束时间不能比开始时间晚七天
        line = line.replaceAll("##12##", c11);

        // 计费标识
        String c21 = UUID.randomUUID().toString();
        line = line.replaceAll("##21##", c21);

        // 原始被叫标识 当##4##为0和1时为空、当##4##为4时必须为合法的手机号码
        String c40 = null;
        if ("0".equals(c4) || "1".equals(c4)) {
            c40 = "";
            line = line.replaceAll("##40##", c40);
        } else {
            c40 = c7;
            line = line.replaceAll("##40##", c40);
        }
        // ##41##:计费方标识       当##4##为0时与##7##一致(号码一致即可)、当##4##为1时与##9##一致、当##4##为4时与##40##一致
        String c41 = null;
        if ("0".equals(c4)) {
            c41 = c7;
            line = line.replaceAll("##41##", c41);
        } else if ("1".equals(c4)){
            c41 = c9;
            line = line.replaceAll("##41##", c41);
        } else {
            c41 = c40;
            line = line.replaceAll("##41##", c41);
        }
        return line;
    }

    private int cutCount(int index, int listSize) {
        if (index > listSize * 10000) {
            return cutCount(index - (listSize * 10000), listSize);
        }
        return index / 10000;
    }

    /**
     * 生成异常数据
     * TODO
     * @return
     */
    private String getErrorMessage(int index) {
        {
            return errorMessage.get(index % errorMessage.size());
        }
    }
}
