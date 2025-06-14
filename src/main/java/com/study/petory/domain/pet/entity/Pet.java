package com.study.petory.domain.pet.entity;

import com.study.petory.common.entity.TimeFeatureBasedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tb_pet")
@NoArgsConstructor
public class Pet extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String size;

	private String species;

	private String gender;

	private String birthday;

	private String photo;
}
