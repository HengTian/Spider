package online.hengtian.hengtian.spider;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;


public class App {
	
	public static void main(String[] args) {
		List<Film> films = Collections.synchronizedList(new LinkedList<>());
		ExecutorService pool = Executors.newFixedThreadPool(4);
		String url = "https://movie.douban.com/top250";
		pool.execute(new Spider(url, films));
		for (int i = 1; i < 10; i++) {
			url = String.format("https://movie.douban.com/top250?start=%d&filter=", 25 * i);
			pool.execute(new Spider(url, films));
		}
		pool.shutdown();
		//等待,当线程池的任务全部执行完之后,isTerminated返回true
		while (true) {
			if (pool.isTerminated()) {
				try {
					
					//保存到数据库
					//获得会话工厂(数据库的连接池)
					SqlSessionFactory factory;
					factory = new SqlSessionFactoryBuilder().build(new FileReader("config.xml"));
					//从会话工场获取一个连接
					SqlSession session = factory.openSession();
					//获得一个mapper(反射:黑魔法)
					FilmMapper mapper=session.getMapper(FilmMapper.class);
					
					//存入数据库
					for(Film f:films) {
						System.out.println(f.toString());
						mapper.insert(f);
					}
					session.commit();//会话提交
					session.close();
					System.out.println("存储成功");
					DownImage(films);
				}catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void DownImage(List<Film> films) {
		// TODO Auto-generated method stub
		ExecutorService pool = Executors.newFixedThreadPool(8);
		for (Film film : films) {
			pool.execute(new ImgDownload(film.poster,"top"+film.getId(),"/home/hengtian/TopImages"));
		}
		pool.shutdown();
	}
}
 