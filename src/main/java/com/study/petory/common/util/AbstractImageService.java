package com.study.petory.common.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractImageService<T> {

	protected final S3Uploader s3Uploader;

	public AbstractImageService(S3Uploader s3Uploader) {
		this.s3Uploader = s3Uploader;
	}

	// 파일 업로드 및 저장
	public List<String> uploadAndSaveAll(List<MultipartFile> files, Object context) {
		List<String> urls = new ArrayList<>();

		if (files == null || files.isEmpty()) {
			return urls; //파일이 없을 때 빈 리스트 반환
		}

		for (MultipartFile file : files) {
			String url = s3Uploader.uploadFile(file, getFolderName());
			T entity = createImageEntity(url, context);
			save(entity);
			urls.add(url);
		}

		return urls;
	}

	protected abstract String getFolderName(); // 도메인별 폴더명

	protected abstract T createImageEntity(String url, Object context); // 도메인별 엔티티 생성

	protected abstract void save(T entity); // 도메인별 저장로직

}
