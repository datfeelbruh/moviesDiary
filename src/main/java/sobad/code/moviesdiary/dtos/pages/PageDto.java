package sobad.code.moviesdiary.dtos.pages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageDto {
    private Integer page;
    private Integer pages;
    private Long total;
    private Integer limit;
}
