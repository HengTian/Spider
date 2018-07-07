package online.hengtian.hengtian.spider;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;


/**
 * 图片下载
 * @author hengtian
 *
 */
public class ImgDownload implements Runnable{
	
	/**
	 * 链接
	 */
	String httpurl;
	
	/**
	 * 文件名
	 */
	String name;
	
	/**
	 * 文件下载路径
	 */
	String path;
	
	/**
	 * 构造方法
	 * @param 下载网址
	 * @param 图片名字
	 * @param 下载存放路径
	 */
	public ImgDownload(String url, String name, String path) {
		this.httpurl = url;
		this.name = name;
		this.path = path;
	}

	/**
	 * 下载文件
	 */
	public void run() {
		try {
			URL url = new URL(httpurl);
			// 访问资源
			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			// 设置超时间为3秒
			conn.setConnectTimeout(3 * 1000);
			
			// 防止屏蔽程序抓取而返回403错误
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			//长度可变的内存数据
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// 从连接对象获取输入流
			try(InputStream inputStream = conn.getInputStream()){
				byte[] buffer = new byte[1024*8];
				int len;
				while ((len = inputStream.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			//转为字符数组->写入缓冲
			byte []data=bos.toByteArray();
			bos.close();
			// 检测文件保存路径是否存在，若不存在，则创建
			File saveDir = new File(path);
			if (!saveDir.exists()) {
				saveDir.mkdir();
			}
			File file = new File(path + "/" + name + ".jpg");
			try(FileOutputStream out=new FileOutputStream(file)){
				out.write(data);
				out.close();
				System.out.println("下载成功:"+path+name);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}