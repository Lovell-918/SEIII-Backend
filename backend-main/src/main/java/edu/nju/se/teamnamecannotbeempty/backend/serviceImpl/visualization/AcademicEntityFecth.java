package edu.nju.se.teamnamecannotbeempty.backend.serviceImpl.visualization;

import edu.nju.se.teamnamecannotbeempty.backend.config.parameter.EntityMsg;
import edu.nju.se.teamnamecannotbeempty.backend.vo.AcademicEntityItem;
import edu.nju.se.teamnamecannotbeempty.backend.vo.AcademicEntityVO;
import edu.nju.se.teamnamecannotbeempty.backend.vo.SimplePaperVO;
import edu.nju.se.teamnamecannotbeempty.backend.vo.TermItem;
import edu.nju.se.teamnamecannotbeempty.data.domain.*;
import edu.nju.se.teamnamecannotbeempty.data.repository.*;
import edu.nju.se.teamnamecannotbeempty.data.repository.popularity.PaperPopDao;
import edu.nju.se.teamnamecannotbeempty.data.repository.popularity.TermPopDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcademicEntityFecth {

    private final AffiliationDao affiliationDao;
    private final AuthorDao authorDao;
    private final ConferenceDao conferenceDao;
    private final PaperDao paperDao;
    private final TermPopDao termPopDao;
    private final EntityMsg entityMsg;
    private final PaperPopDao paperPopDao;

    @Autowired
    public AcademicEntityFecth(AffiliationDao affiliationDao, AuthorDao authorDao, ConferenceDao conferenceDao,
                             PaperDao paperDao, EntityMsg entityMsg,
                             TermPopDao termPopDao, PaperPopDao paperPopDao) {
        this.affiliationDao = affiliationDao;
        this.authorDao = authorDao;
        this.conferenceDao = conferenceDao;
        this.paperDao = paperDao;
        this.entityMsg = entityMsg;
        this.termPopDao = termPopDao;
        this.paperPopDao = paperPopDao;
    }

    @Cacheable(value = "getAcedemicEntity", key = "#p0+'_'+#p1")
    public AcademicEntityVO getAcedemicEntity(long id, int type) {
        AcademicEntityVO academicEntityVO=null;
        if(type==entityMsg.getAuthorType()) academicEntityVO=authorsEntity(id);
        else if(type==entityMsg.getAffiliationType()) academicEntityVO=affilicationEntity(id);
        else if(type==entityMsg.getConferenceType()) academicEntityVO=conferenceEntity(id);
        return academicEntityVO;
    }

    private AcademicEntityVO authorsEntity(long id) {
        List<AcademicEntityItem> affiEntityItems = generateAffiEntityItems(affiliationDao.getAffiliationsByAuthor(id));
        List<AcademicEntityItem> conferenceEntityItems = generateConferenceEntityItems(conferenceDao.getConferencesByAuthor(id));
        List<TermItem> termItems = convertToTermIterm(termPopDao.getTermPopByAuthorID(id));
        List<SimplePaperVO> simplePaperVOS = generateTopPapers(paperPopDao.findTopPapersByAuthorId(id));

        return new AcademicEntityVO(entityMsg.getAuthorType(), id,authorDao.findById(id).get().getName(),
                (int)paperDao.getCitationByAuthorId(id),null,affiEntityItems,conferenceEntityItems,termItems,
                simplePaperVOS);
    }

    private AcademicEntityVO affilicationEntity(long id){
        List<AcademicEntityItem> authorEntityItems = generateAuthorEntityItems(authorDao.getAuthorsByAffiliation(id));
        List<AcademicEntityItem> conferenceEntityItems = generateConferenceEntityItems(conferenceDao.getConferencesByAffiliation(id));
        List<TermItem> termItems = convertToTermIterm(termPopDao.getTermPopByAffiID(id));
        List<SimplePaperVO> simplePaperVOS = generateTopPapers(paperPopDao.findTopPapersByAuthorId(id));

        return new AcademicEntityVO(entityMsg.getAffiliationType(),id,affiliationDao.findById(id).get().getName(),
                (int)paperDao.getCitationByAffiId(id),authorEntityItems,null,conferenceEntityItems,termItems,
                simplePaperVOS);
    }

    private AcademicEntityVO conferenceEntity(long id){
        List<AcademicEntityItem> authorEntityItems = generateAuthorEntityItems(authorDao.getAuthorsByConference(id));
        List<AcademicEntityItem> affiEntityItems = generateAffiEntityItems(affiliationDao.getAffiliationsByConference(id));
        List<TermItem> termItems=convertToTermIterm(termPopDao.getTermPopByConferenceID(id));
        List<SimplePaperVO> simplePaperVOS = generateTopPapers(paperPopDao.findTopPapersByConferenceId(id));

        return new AcademicEntityVO(entityMsg.getConferenceType(),id,conferenceDao.findById(id).get().buildName(),-1,
                authorEntityItems,affiEntityItems,null,termItems,simplePaperVOS);
    }

    private List<AcademicEntityItem> generateAuthorEntityItems(List<Author> authors) {
        List<AcademicEntityItem> academicEntityItems = authors.stream().map(
                author -> new AcademicEntityItem(entityMsg.getAuthorType(), author.getActual().getId(), author.getName()))
                .collect(Collectors.toList());
        return academicEntityItems.size()>15? academicEntityItems.subList(0,15):academicEntityItems;
    }

    private List<AcademicEntityItem> generateAffiEntityItems(List<Affiliation> affiliations) {
        List<AcademicEntityItem> academicEntityItems = affiliations.stream().map(
                affiliation -> new AcademicEntityItem(entityMsg.getAffiliationType(), affiliation.getId(), affiliation.getName()))
                .collect(Collectors.toList());
        return academicEntityItems.size()>15? academicEntityItems.subList(0,15):academicEntityItems;
    }

    private List<AcademicEntityItem> generateConferenceEntityItems(List<Conference> conferences) {
        List<AcademicEntityItem> academicEntityItems =  conferences.stream().map(
                conference -> new AcademicEntityItem(entityMsg.getConferenceType(), conference.getId(), conference.buildName()))
                .collect(Collectors.toList());
        return academicEntityItems.size()>15? academicEntityItems.subList(0,15):academicEntityItems;
    }

    private List<TermItem> convertToTermIterm(List<Term.Popularity> termPopularityList) {
        return termPopularityList.stream().map(
                termPopularity -> new TermItem(termPopularity.getTerm().getId(), termPopularity.getTerm().getContent(), termPopularity.getPopularity())
        ).collect(Collectors.toList());
    }

    private List<SimplePaperVO> generateTopPapers(List<Paper.Popularity> paperPopularityList){
        List<SimplePaperVO> simplePaperVOS = paperPopularityList.stream()
                .map(paprePopularity -> new SimplePaperVO(paprePopularity.getPaper()))
                .collect(Collectors.toList());
        return simplePaperVOS.size()>5? simplePaperVOS.subList(0,5):simplePaperVOS;
    }

}