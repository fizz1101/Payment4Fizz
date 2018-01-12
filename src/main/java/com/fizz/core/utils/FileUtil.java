package com.fizz.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class FileUtil {
	
	/**
	 * 读取文件内容
	 * @date 2017年1月11日
	 * @author 张纯真
	 * @param filePath
	 * @param encoding
	 * @return
	 */
	public static List<List<String>> readFile(String filePath, String encoding) {
		List<List<String>> list_txt = new ArrayList<List<String>>();
		BufferedReader bufferedReader = null;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	String[] cellArr = lineTxt.split(",");
                	List<String> list_cell = new ArrayList<String>();
                	for (int i=0; i<cellArr.length; i++) {
                		String content = cellArr[i];
                		if (content != null && !"".equals(content)) {
                			list_cell.add(cellArr[i].trim());
                		}
                	}
                	list_txt.add(list_cell);
                }
	        } else {
	            System.out.println("找不到指定的文件");
	        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        } finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return list_txt;
    }
	
	/**
	 * 获取目录下的文件(根据文件名)
	 * @date 2017年9月30日
	 * @author 张纯真
	 * @param path
	 * @param fileNames
	 * @param order 1:asc;2:desc
	 * @return
	 */
	public static List<File> getFileListByName(String path, List<String> fileNames, int order) {
		List<File> list_res = new ArrayList<File>();
		List<File> list_file = new ArrayList<File>();
		int size = fileNames.size();
		File file_path = new File(path);
		File[] files = file_path.listFiles();
		if (files != null) {
			int count = 0;
			for (File f : files) {
				if (f.isFile()) {
					list_file.add(f);
				}
			}
			switch (order) {
			case 1:
				Collections.sort(list_file, new Comparator<File>() {
		    		@Override
		    		public int compare(File o1, File o2) {
		    	        if (o1.isDirectory() && o2.isFile())
		    	            return -1;
		    	        if (o1.isFile() && o2.isDirectory())
		    	            return 1;
		    	        return o1.getName().compareTo(o2.getName());
		    	    }
				});
				break;
			case 2:
				Collections.sort(list_file, new Comparator<File>() {
		    		@Override
		    		public int compare(File o1, File o2) {
		    	        if (o1.isDirectory() && o2.isFile())
		    	            return -1;
		    	        if (o1.isFile() && o2.isDirectory())
		    	            return 1;
		    	        return o2.getName().compareTo(o1.getName());
		    	    }
				});
			default:
				break;
			}
			for (File file : list_file) {
				String fileName = file.getName();
				if (fileNames.contains(fileName)) {
					list_res.add(file);
					count++;
				}
				if (count == size) {
					break;
				}
			}
		}
		return list_res;
	}
  
    /**
     * 获取目录下的文件并排序
     * @date 2017年1月13日
     * @author 张纯真
     * @param path
     * @param extName
     * @return
     */
    public static List<File> getFileListSort(String path, String extName, int limit) {
    	List<File> list_file = new ArrayList<File>();
    	File file = new File(path);
    	File[] files = file.listFiles();
    	if (files != null) {
	    	for (File f : files) {
	    		if (f.isFile() && f.getName().endsWith(extName)) {
	    			list_file.add(f);
	    		}
	    	}
    	}
    	Collections.sort(list_file, new Comparator<File>() {
    		@Override
    		public int compare(File o1, File o2) {
    	        if (o1.isDirectory() && o2.isFile())
    	            return -1;
    	        if (o1.isFile() && o2.isDirectory())
    	            return 1;
    	        return o1.getName().compareTo(o2.getName());
    	    }
		});
    	if (limit > 0 && list_file.size()>limit) {
    		list_file = list_file.subList(0, limit);
    	}
    	return list_file;
    }
    
    /**
     * 移动文件到目标目录
     * @date 2017年1月20日
     * @author 张纯真
     * @param file
     * @param destPath
     * @param newName
     * @return
     */
    public static boolean moveFile(File file, String destPath, String newName) {
    	boolean flag = false;
    	File folder = new File(destPath);
    	if (!folder.exists()) {
    		folder.mkdirs();
    	}
    	if (StringUtils.isEmpty(newName)) {
    		newName = file.getName();
    	}
    	File file_new = new File(destPath + newName);
    	if (file.exists() && file.renameTo(file_new)) {
    		flag = true;
    	}
    	return flag;
    }
  
    /** 
     * 删除该目录下的所有文件 
     * @param filePath 
     *            文件目录路径 
     */  
    public static void deleteFiles(String filePath) {  
        File file = new File(filePath);  
        if (file.exists()) {  
            File[] files = file.listFiles();  
            for (int i = 0; i < files.length; i++) {  
                if (files[i].isFile()) {  
                    files[i].delete();  
                }  
            }  
        }  
    }  
  
    /** 
     * 删除单个文件 
     * @param filePath 
     *         文件目录路径 
     * @param fileName 
     *         文件名称 
     */  
    public static void deleteFile(String filePath, String fileName) {  
        File file = new File(filePath + fileName);  
        if (file.exists()) {
        	file.delete();
        }  
    }  
    
    /** 
     * 下载文件 
     * @param response 
     * @param csvFilePath 
     *              文件路径 
     * @param fileName 
     *              文件名称 
     * @throws IOException 
     */  
    public static void exportFile(HttpServletResponse response, String csvFilePath, String fileName)  
                                                                                                    throws IOException {  
        response.setContentType("application/csv;charset=GBK");  
        response.setHeader("Content-Disposition",  
            "attachment;  filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));  
        //URLEncoder.encode(fileName, "GBK")  
  
        InputStream in = null;  
        try {  
            in = new FileInputStream(csvFilePath);  
            int len = 0;  
            byte[] buffer = new byte[1024];  
            response.setCharacterEncoding("GBK");  
            OutputStream out = response.getOutputStream();  
            while ((len = in.read(buffer)) > 0) {  
                //out.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });  
                out.write(buffer, 0, len);  
            }  
        } catch (FileNotFoundException e) {  
            System.out.println(e);  
        } finally {  
            if (in != null) {  
                try {  
                    in.close();  
                } catch (Exception e) {  
                    throw new RuntimeException(e);  
                }  
            }  
        }  
    }  
  
    /** 
     * 测试数据 
     * @param args 
     */  
    //@SuppressWarnings({ "rawtypes", "unchecked" })  
    public static void main(String[] args) {  
        /*List exportData = new ArrayList<Map>();  
        Map row1 = new LinkedHashMap<String, String>();  
        row1.put("1", "11");  
        row1.put("2", "12");  
        row1.put("3", "13");  
        row1.put("4", "14");  
        exportData.add(row1);  
        row1 = new LinkedHashMap<String, String>();  
        row1.put("1", "21");  
        row1.put("2", "22");  
        row1.put("3", "23");  
        row1.put("4", "24");  
        exportData.add(row1);  
        LinkedHashMap map = new LinkedHashMap();  
        map.put("1", "第一列");  
        map.put("2", "第二列");  
        map.put("3", "第三列");  
        map.put("4", "第四列");  
  
        String path = "f:/export/";  
        String fileName = "文件导出";  
        File file = FileUtil.createCSVFile(exportData, map, path, fileName);  
        String fileName2 = file.getName();  
        System.out.println("文件名称：" + fileName2);*/  
    	String path = "E:\\test";
    	List<File> list = getFileListSort(path, ".txt", -1);
    	System.out.println(list.toString());
    }  

}
