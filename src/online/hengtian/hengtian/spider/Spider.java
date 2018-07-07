package online.hengtian.hengtian.spider;

import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * 网络爬虫
 * 
 * @author hengtian
 *
 */
public class Spider implements Runnable{
	/**
	 * 构造方法
	 * @param url 网站的路径
	 */
	String url;
	private List<Film> films;
	public Spider(String url, List<Film> films) {
		this.url = url;
		this.films = films;
	}
	public void run() {
		// TODO Auto-generated method stub
		try {
			org.jsoup.nodes.Document doc=Jsoup.connect(url).get();
//			//标题
//			System.out.println(doc.title());
//			//文字
//			System.out.println(doc.text());
//			//
//			System.out.println(doc.data());
			//源文件
//			System.out.println(doc.html());
//			Elements es=doc.select("a .title:eq(0)");
//			System.out.println(es.size());
//			for(Element temp:es) {
//				System.out.println(temp.text());
//			}
			Elements es=doc.select(".grid_view .item");
//			System.out.println(es.size());
			for(Element e:es) {
				Film film=new Film();
				film.title=e.select(".info .title").get(0).text();
				//attr("属性")得到标签的属性
				film.poster=e.select(".pic img").get(0).attr("src");
				String ratingString[]=e.select(".star span").get(3).text().split("人");
				film.rating=ratingString[0];
				film.star=Double.parseDouble(e.select(".star .rating_num").get(0).text());
				film.id=Integer.parseInt(e.select("em").get(0).text());
				film.quote=e.select(".quote .inq").text();  
				String others=e.select(".info .bd p").get(0).text();
//				System.out.println(others);
				//正则分隔导演和主演
				String getDirectorReg="\\s主演";
				String []directors=others.split(getDirectorReg);
				film.director=directors[0];
				System.out.println(film.director);
				//正则取出上映时间
				String getTimeReg="\\d{4}";
				Pattern pattern=Pattern.compile(getTimeReg);
				if(directors.length>1) {
					Matcher matcher = pattern.matcher(directors[1]); 
				    if(matcher.find()) film.time=Integer.parseInt(matcher.group());
				//正则通过时间分隔出主演
					String getActReg="\\s\\d{4}";
					String []actors=directors[1].split(getActReg);
					film.actor="主演"+actors[0];
//					System.out.println(film.actor);
				//分隔出国家和剧情
					if(actors.length>1) {
						String getCountryReg="/\\s";
						String []last=actors[1].split(getCountryReg);
//						for(String a:last) {
//							System.out.println(a);
//						}
						film.country=last[1];
						film.type=last[2];
					}
					
				}
				else {
					Matcher matcher = pattern.matcher(directors[0]); 
				    if(matcher.find()) film.time=Integer.parseInt(matcher.group());
				    //正则通过时间分隔出主演
					String getActReg="\\s\\d{4}";
					String []actors=directors[0].split(getActReg);
					//无主演时导演要重新获取
					film.director=actors[0];
					//没有directors[1]说明没主演
					film.actor="无主演";
//					System.out.println(film.actor);
				//分隔出国家和剧情
					if(actors.length>1) {
						String getCountryReg="/\\s";
						String []last=actors[1].split(getCountryReg);
//						for(String a:last) {
//							System.out.println(a);
//						}
						film.country=last[1];
						film.type=last[2];
					}
				}
				films.add(film);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
