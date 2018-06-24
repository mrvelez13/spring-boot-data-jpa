package com.typefy.springboot.app.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity //Indicando que esta clase es una entidad que representa una tabla de base de datos. O más específicamente, un objeto de una tabla.
@Table(name="clientes") //Nombre de la tabla de base de datos
public class Cliente implements Serializable
{
	private static final long serialVersionUID = 1L; //Opcional para las clases que implementan Serializable y es administrado internamente por la VM

	@Id //Indica que el campo es una clave primaria
	@GeneratedValue(strategy = GenerationType.IDENTITY) //Indica que el valor es autogenerado e incremental
	private Long id;
	
	//Si los nombres de los atributos de la clase son exactamente iguales a los nombres de la tabla de BD, no es  necesario
	//usar la anotación @column. De lo contrario habría que indicar a JPA textualmente cuál es la columna a la cual el campo está haciendo referencia.
	@NotEmpty
	private String nombre;
	@NotEmpty
	private String apellido;
	@NotEmpty
	@Email
	private String email;
	
	@NotNull
	@Column(name="create_at")//Indica la columna de BD a la cual se hace referencia
	@Temporal(TemporalType.DATE)//Permite darle formato a un dato. En este caso, a un dato de tipo fecha. TIME es para tipo hora y TIMESTAMP para tipo fecha hora.
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createAt;
	
	@Column(name="foto")
	private String imagen;
	
//	@PrePersist //Este método se va a ejecutar antes de que el dato se guarde en la BD
//	public void prePersist()
//	{
//		createAt = new Date();
//	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public String getImagen() {
		return imagen;
	}
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
}
