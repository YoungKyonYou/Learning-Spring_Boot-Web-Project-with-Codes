package org.zerock.guestbook.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.guestbook.dto.GuestbookDTO;
import org.zerock.guestbook.dto.PageRequestDTO;
import org.zerock.guestbook.dto.PageResultDTO;
import org.zerock.guestbook.entity.Guestbook;
import org.zerock.guestbook.entity.QGuestbook;
import org.zerock.guestbook.repository.GuestbookRepository;

import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class GuestbookServiceImpl implements GuestbookService {

    private final GuestbookRepository repository;

    @Override
    public Long register(GuestbookDTO dto) {

        log.info("DTO------------------------");
        log.info(dto);

        Guestbook entity = dtoToEntity(dto);

        log.info(entity);

        repository.save(entity);

        return entity.getGno();
    }

//    @Override
//    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO) {
//
//        Pageable pageable = requestDTO.getPageable(Sort.by("gno").descending());
//
//        Page<Guestbook> result = repository.findAll(pageable);
//
//        Function<Guestbook, GuestbookDTO> fn = (entity -> entityToDto(entity));
//
//        return new PageResultDTO<>(result, fn );
//    }

    @Override
    public PageResultDTO<GuestbookDTO, Guestbook> getList(PageRequestDTO requestDTO){
        //가장 최근에 쓴것은 db에서 gno가 젤 큰 수이기 때문에 내림 차순으로 하는 것
        Pageable pageable=requestDTO.getPageable(Sort.by("gno").descending());
        BooleanBuilder booleanBuilder=getSearch(requestDTO);
        //pageable에서 페이지 설정한 값으로 리포지토리를 뒤져서 그 페이지 만큼 Page 타입으로 반환
        //밑에서 그 Page<Guestbook> 타입으로 받은 엔티티 result는 나중에 GuestbookDTO로 바꾸는 과정을 겪게 됨
        //즉 지금 pageable에서 설정한 페이지만큼 리포지토리를 뒤져서 그 만큼 Page 타입으로 반환
        Page<Guestbook> result=repository.findAll(booleanBuilder,pageable);
        //Page<Guestbook>에서 Guestbook 엔티티를 GuestbookDto로 바꾸는 것
        //GuesbookService 인터페이스에서 default로 둔 entityToDto로 보내서 GuestbookDto로 바꿈
        // https://steady-coding.tistory.com/308 함수적 인터페이스 Function  참고
        //Function<T, U>는 객체 T를 객체 U로 매핑한다는 것
        //Guestbook형인 entity를 GuesetbookDTO로 변환해주기 위해서 <>를 저렇게 함. 그래서 나중에
        //entityToDto를 거치면 GuestbookDTO가 되니까 저런식으로 구성해줘야 함
        Function<Guestbook, GuestbookDTO> fn=(entity->entityToDto(entity));
        //PageResultDTO클래스에서는 Page<Entity>의 엔티티 객체들을 DTO 객체로 변환해서 자료구조에 담아주는 역할을 함
        //그래서 result라는 Page<Guestbook> 타입 객체와 GuestbookDTO 타입을 그 클래스로 보내는 것임.
        return new PageResultDTO<>(result, fn);
    }


    @Override
    public GuestbookDTO read(Long gno) {

        Optional<Guestbook> result = repository.findById(gno);

        return result.isPresent()? entityToDto(result.get()): null;
    }

    @Override
    public void remove(Long gno) {

        repository.deleteById(gno);

    }

    @Override
    public void modify(GuestbookDTO dto) {

        //업데이트 하는 항목은 '제목', '내용'

        Optional<Guestbook> result = repository.findById(dto.getGno());

        if(result.isPresent()){

            Guestbook entity = result.get();

            entity.changeTitle(dto.getTitle());
            entity.changeContent(dto.getContent());

            repository.save(entity);

        }
    }


    private BooleanBuilder getSearch(PageRequestDTO requestDTO){

        String type = requestDTO.getType();

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QGuestbook qGuestbook = QGuestbook.guestbook;

        String keyword = requestDTO.getKeyword();

        BooleanExpression expression = qGuestbook.gno.gt(0L); // gno > 0 조건만 생성

        booleanBuilder.and(expression);

        if(type == null || type.trim().length() == 0){ //검색 조건이 없는 경우
            return booleanBuilder;
        }


        //검색 조건을 작성하기
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if(type.contains("t")){
            conditionBuilder.or(qGuestbook.title.contains(keyword));
        }
        if(type.contains("c")){
            conditionBuilder.or(qGuestbook.content.contains(keyword));
        }
        if(type.contains("w")){
            conditionBuilder.or(qGuestbook.writer.contains(keyword));
        }

        //모든 조건 통합
        booleanBuilder.and(conditionBuilder);

        return booleanBuilder;
    }

}
