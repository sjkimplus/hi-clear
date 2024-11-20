package com.play.hiclear.domain.club.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Document(indexName = "club_index")
@Builder
public class ClubDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private Long id;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String clubname;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String regionAddress;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String roadAddress;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String intro;

}
