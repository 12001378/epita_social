package com.epita.social.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@RequiredArgsConstructor
public class Message {
	
	@Id
	@GeneratedValue
	private UUID id;
	private String content;
	private String image;
	
	private LocalDateTime timeStamp;
	private Boolean is_read;
	
	@ManyToOne
	@JdbcType(VarcharJdbcType.class)
	private User user;
	
	@ManyToOne
	@JoinColumn(name="chat_id")
	@JdbcType(VarcharJdbcType.class)
	private Chat chat;
	
	

	

}
