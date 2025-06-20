package com.study.petory.domain.album.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.AbstractImageService;
import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumImage;
import com.study.petory.domain.album.repository.AlbumImageRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.UserRole;

@Service
public class AlbumImageServiceImpl extends AbstractImageService<AlbumImage> {

	private final AlbumImageRepository albumImageRepository;

	public AlbumImageServiceImpl(S3Uploader s3Uploader, AlbumImageRepository albumImageRepository) {
		super(s3Uploader);
		this.albumImageRepository = albumImageRepository;
	}

	@Override
	@Transactional
	public void deleteImage(AlbumImage image) {
		deleteImageInternal(image);
	}

	@Override
	protected String getFolderName() {
		return "album-image";
	}

	@Override
	protected AlbumImage createImageEntity(String url, Object context) {
		return new AlbumImage(url, (Album)context);
	}

	@Override
	@Transactional
	protected void save(AlbumImage entity) {
		albumImageRepository.save(entity);
	}

	@Override
	public AlbumImage findImageById(Long imageId) {
		return albumImageRepository.findById(imageId)
			.orElseThrow(() -> new CustomException(ErrorCode.ALBUM_IMAGE_NOT_FOUND));
	}

	@Override
	protected String getImageUrl(AlbumImage image) {
		return image.getUrl();
	}

	public int findImageSize(List<UserRole> userRole) {
		int imageSize = 1;
		if (userRole.contains(Role.USER)) {
			imageSize = 1;
		}
		if (userRole.contains(Role.ADMIN)) {
			imageSize = 5;
		}
		return imageSize;
	}
}
