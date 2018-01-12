package enterovirus.protease.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "git", name = "traceability_map")
public class TraceabilityMapBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name="upstream_item_id")
	private TraceableItemBean upstreamItem;
	
	@ManyToOne
	@JoinColumn(name="downstream_item_id")
	private TraceableItemBean downstreamItem;
	
	/*
	 * Hibernate constructor
	 */
	public TraceabilityMapBean () {
		
	}

	public TraceabilityMapBean(
			TraceableItemBean upstreamItem,
			TraceableItemBean downstreamItem) {
		
		this.upstreamItem = upstreamItem;
		this.downstreamItem = downstreamItem;
	}
}
