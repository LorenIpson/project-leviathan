package game_forum_api.ads.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Ads")
public class Ad {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "imageUrl", nullable = false)
	    private String imageUrl;

	    @Column(name = "redirectUrl", nullable = false)
	    private String redirectUrl;

	    @Column(name = "position", nullable = false)
	    private String position;

	    @Column(name = "startTime", nullable = false)
	    private LocalDateTime startTime;

	    @Column(name = "endTime", nullable = false)
	    private LocalDateTime endTime;

	    @Column(name = "isActive", nullable = false)
	    private Boolean isActive = true;

	    @Column(name = "width")
	    private Integer width;

	    @Column(name = "height")
	    private Integer height;
	    
	    private Integer sortOrder;
}
