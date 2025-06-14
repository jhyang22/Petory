package com.study.petory.common.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractImageService<T> {

	@Value("${cloud.aws.bucket}")
	private String bucket;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	protected final S3Uploader s3Uploader;

	public AbstractImageService(S3Uploader s3Uploader) {
		this.s3Uploader = s3Uploader;
	}

	/**
	 * 이미지 파일 업로드 및 저장
	 * @param files 업로드된 이미지 파일
	 * @param context 객체(도메인)
	 * @return 이미지 url List
	 */
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

	/**
	 * 이미지 파일 업로드 및 저장 (entity 반환)
	 * @param files 업로드된 이미지 파일
	 * @param context 객체(도메인)
	 * @return 이미지 url List
	 */
	public List<T> uploadAndReturnEntities(List<MultipartFile> files, Object context) {
		List<T> imageEntities = new ArrayList<>();

		if (files == null || files.isEmpty()) {
			return imageEntities; //파일이 없을 때 빈 리스트 반환
		}

		for (MultipartFile file : files) {
			String url = s3Uploader.uploadFile(file, getFolderName());
			T entity = createImageEntity(url, context);
			save(entity);
			imageEntities.add(entity);
		}

		return imageEntities;
	}

	/**
	 * 이미지 파일 삭제
	 * @param image 도메인 별 이미지엔티티 받기
	 */
	@Transactional
	public void deleteImageInternal(T image) {
		String key = extractKeyFromUrl(getImageUrl(image));
		s3Uploader.deleteFile(key);
	}

	// 공통 유틸
	protected String extractKeyFromUrl(String url) {
		String baseUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
		return url.replace(baseUrl, "");
	}

	// 도메인별로 구현 필요(업로드 및 저장 용도)
	protected abstract String getFolderName(); // 도메인별 폴더명

	protected abstract T createImageEntity(String url, Object context); // 도메인별 엔티티 생성

	protected abstract void save(T entity); // 도메인별 DB 저장로직

	// 도메인별 구현 필요(삭제 용도)
	public abstract void deleteImage(T image); // deleteImageInternal 메서드 구현클래스에서 호출

	protected abstract T findImageById(Long imageId); // Image 조회로직

	protected abstract String getImageUrl(T image); // Image에서 Url 가져오기

}
