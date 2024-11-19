package com.play.hiclear.domain.club.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.elasticsearch.geometry.Point;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

@Getter
@Builder
@Document(indexName = "clubs")
public class ClubDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String clubname;
    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String intro;
    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String regionAddress;
    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String roadAddress;

    public ClubDocument(String id, String clubname, String intro, String regionAddress, String roadAddress) {
        this.id = id;
        this.clubname = clubname;
        this.intro = intro;
        this.regionAddress = regionAddress;
        this.roadAddress = roadAddress;
    }
}
