package org.zerock.guestbook.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.List;
import java.util.stream.IntStream;

@Data
public class PageResultDTO<DTO, EN> {
    private List<DTO> dtoList;
    //총 페이지 번호
    private int totalPage;
    //현재 페이지 번호
    private int page;
    //목록 사이즈
    private int size;
    //시작 페이지 번호, 끝 페이지 번호
    private int start, end;
    //이전, 다음
    private boolean prev, next;
    //페이지 번호 목록
    private List<Integer> pageList;

    //생성자
    //result를 리스트로 변환
    public PageResultDTO(Page<EN> result, Function<EN,DTO> fn){
        //Page<EN>형인 result를 스트림형으로 바꾸고 map(fn)해서 EN->DTO로 바꾸고 list에 담는 과정
        dtoList=result.stream().map(fn).collect(Collectors.toList());
        totalPage=result.getTotalPages();
        makePageList(result.getPageable());
    }
    private void makePageList(Pageable pageable){
        this.page=pageable.getPageNumber()+1;//0부터 시작하므로 1을 추가
        this.size=pageable.getPageSize();

        //tempEnd=10, page=1
        int tempEnd=(int)(Math.ceil(page/10.0))*10;
        //start=1
        start=tempEnd-9;
        //prev=0
        prev=start>1;

        //totalPage=31 end=10
        System.out.println("테스트 테스트 start: "+start+" page: "+page+" tempEnd: "+tempEnd+" size: "+size);
        end=totalPage>tempEnd?tempEnd:totalPage;
        System.out.println("테스트 테스트 totalPage: "+totalPage+" end: "+end);
        //next=1
        next=totalPage>end;
        pageList= IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());
    }
}
