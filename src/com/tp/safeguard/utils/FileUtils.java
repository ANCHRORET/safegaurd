package com.tp.safeguard.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

public class FileUtils {
	/**
	 * zip压缩 
	 * 注意:流已经释放
	 * 
	 * @param is
	 *            要压缩的文件对应的标准输入流
	 * @param os
	 *            压缩后的文件对应的标准输出流
	 * @throws IOException
	 */
	public static void zipFile(InputStream is, OutputStream os)
			throws IOException {
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(os);
			byte[] bys = new byte[1024];
			int len = 0;
			while ((len = is.read(bys)) != -1) {
				gos.write(bys, 0, len);
				gos.flush();
			}
		} finally {
			closeIO(is);
			closeIO(gos);
		}
	}

	/**
	 * zip压缩 
	 * 注意:流已经释放
	 * 
	 * @param file 被压缩的文件
	 * @param zipFile 压缩后的文件对应的文件对象
	 * @throws IOException
	 */
	public static void zipFile(File file, File zipFile) throws IOException {
		OutputStream os = new FileOutputStream(zipFile);
		InputStream is = new FileInputStream(file);
		zipFile(is, os);
	}

	/**
	 * zip压缩 
	 * 注意:流已经释放
	 * 
	 * @param path
	 *            要压缩文件的对应路径,eg C:\\address.db
	 * @param zipPath
	 *            压缩后的文件对应的路径,eg C:\\address.db.zip
	 * @throws IOException
	 */
	public static void zipFile(String path, String zipPath)
			throws IOException {
		zipFile(new File(path), new File(zipPath));
	}

	/**
	 * 解压缩
	 * 
	 * @param is
	 *            标准输入流
	 * @param os
	 *            标准输出流
	 * @throws IOException
	 */
	public static void unzip(InputStream is, OutputStream os)
			throws IOException {
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(is);
			byte[] bys = new byte[1024];
			int len = 0;
			while ((len = gis.read(bys)) != -1) {
				os.write(bys, 0, len);
				os.flush();
			}
		} finally {
			closeIO(gis);
			closeIO(os);
		}
	}

	/**
	 * 解压文件
	 * 
	 * @param zipFile
	 *            要解压的文件
	 * @param file
	 *            解压为file文件
	 * @throws IOException
	 */
	public static void unzip(File zipFile, File file) throws IOException {
		unzip(new FileInputStream(zipFile), new FileOutputStream(file));
	}

	/**
	 * 解压文件
	 * 
	 * @param zipPath
	 *            要解压文件对应的路径,eg C:\\address.db.zip
	 * @param path
	 *            解压后的文件对应的路径,eg C:\\address.db.zip
	 * @throws IOException
	 */
	public static void unzip(String zipPath, String path) throws IOException {
		unzip(new File(zipPath), new File(path));
	}

	/**
	 * 释放流对应的工具方法
	 * 
	 * @param io
	 */
	private static void closeIO(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			io = null;
		}
	}
}
