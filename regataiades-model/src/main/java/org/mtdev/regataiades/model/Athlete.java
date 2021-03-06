package org.mtdev.regataiades.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "athletes")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Athlete {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	protected int id;

	@Column(name = "name")
	protected String name;

	@Column(name = "surname")
	protected String surname;

	@Column(name = "licence")
	protected String licence;

	@Column(name = "sex")
	protected String sex;

	@Column(name = "birthdate")
	protected String birthdate;

	@Column(name = "captain")
	protected boolean captain;

}
