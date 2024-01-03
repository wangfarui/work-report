package approval.dingtalk.util;

import com.wyyt.scp.biz.core.utils.FilePrefixUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 钉钉FileUtil
 *
 * @author wangfarui
 * @since 2023/8/17
 */
public abstract class DingTalkFileUtil {

    /**
     * wyyt内部文件url地址
     */
    public static final String INTERNAL_URL = "wyyt";

    /**
     * 湖北长捷url地址内容
     */
    public static final String HBCJ_URL = "hbcjwl.cn";

    /**
     * 获取文件流
     * <p>需要自己关闭流</p>
     */
    public static InputStream getInputStream(String fileUrl) {
        fileUrl = FilePrefixUtil.appendUrlPrefix(fileUrl);
        try {
            URL urlObj = new URL(fileUrl);
            URLConnection urlConnection = urlObj.openConnection();
            urlConnection.setConnectTimeout(30 * 1000);
            urlConnection.setReadTimeout(60 * 1000);
            urlConnection.setDoInput(true);
            if (isNeedInternalReferer(fileUrl)) {
                urlConnection.addRequestProperty("Referer", "service.sijibao.com");
            }
            return urlConnection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isNeedInternalReferer(String url) {
        return url.contains(INTERNAL_URL);
    }
}
