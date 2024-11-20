package com.play.hiclear.domain.meeting.entity;

import org.springframework.data.elasticsearch.annotations.Field;
import com.play.hiclear.common.enums.Ranks;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
@Document(indexName = "meeting_nori")
public class MeetingDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private Long id;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori_analyzer")
    private String regionAddress;

    @Field(type = FieldType.Keyword)
    private Ranks ranks;

    public MeetingDocument(Meeting meeting) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.regionAddress = meeting.getRegionAddress();
        this.ranks = meeting.getRanks();
    }
}