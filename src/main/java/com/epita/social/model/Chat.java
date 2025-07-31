package com.epita.social.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
public class Chat {
	@Id
	@GeneratedValue
	private UUID id;
	private String chat_name;
	private String chat_image;
	
	private Boolean is_group;
	
	@ManyToOne
	private User created_by;
	
	@ManyToMany
	private List<User> users = new ArrayList<>();
 
//	@JsonIgnore
	@OneToMany( mappedBy = "chat")
	private List<Message> messages = new ArrayList<>();

	
}
