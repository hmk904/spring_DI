package com.spring.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsp.command.PageMaker;
import com.jsp.command.SearchCriteria;
import com.jsp.dto.AttachVO;
import com.jsp.dto.PdsVO;
import com.jsp.service.PdsService;
import com.spring.dao.AttachDAO;
import com.spring.dao.PdsDAO;

public class PdsServiceImpl implements PdsService {
	private PdsDAO pdsDAO;
	public void setPdsDAO(PdsDAO pdsDAO) {
		this.pdsDAO = pdsDAO;
	}

	private AttachDAO attachDAO;
	public void setAttachDAO(AttachDAO attachDAO) {
		this.attachDAO = attachDAO;
	}

	private void addAttachList(PdsVO pds) throws SQLException {

		if (pds == null)
			return;

		int pno = pds.getPno();
		List<AttachVO> attachList = attachDAO.selectAttachesByPno(pno);

		pds.setAttachList(attachList);
	}

	@Override
	public Map<String, Object> getList(SearchCriteria cri) throws SQLException {
		Map<String, Object> dataMap = new HashMap<String, Object>();

		List<PdsVO> pdsList = pdsDAO.selectPdsCriteria(cri);
		dataMap.put("pdsList", pdsList);

		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(pdsDAO.selectPdsCriteriaTotalCount(cri));
		dataMap.put("pageMaker", pageMaker);

		return dataMap;
	}

	@Override
	public void regist(PdsVO pds) throws SQLException {
		int pno = pdsDAO.selectSeqNextValue();
		pds.setPno(pno);
		pdsDAO.insertPds(pds);

		if (pds.getAttachList() != null) {
			for (AttachVO attach : pds.getAttachList()) {
				attach.setPno(pno);
				attach.setAttacher(pds.getWriter());
				attachDAO.insertAttach(attach);
			}
		}

	}

	@Override
	public PdsVO read(int pno) throws SQLException {
		PdsVO pds = pdsDAO.selectPdsByPno(pno);
		pdsDAO.increaseViewCnt(pno);
		return pds;
	}

	@Override
	public PdsVO getPds(int pno) throws SQLException {
		PdsVO pds = pdsDAO.selectPdsByPno(pno);
		addAttachList(pds);
		return pds;
	}

	@Override
	public void modify(PdsVO pds) throws SQLException {
		pdsDAO.updatePds(pds);
		
		if(pds.getAttachList()!=null)
			for(AttachVO attach : pds.getAttachList())
				attachDAO.insertAttach(attach);
	}

	@Override
	public void remove(int pno) throws SQLException {
		pdsDAO.deletePds(pno);

	}

	@Override
	public AttachVO getAttachByAno(int ano) throws SQLException {
		AttachVO attach = attachDAO.selectAttachByAno(ano);
		return attach;
	}

	@Override
	public void removeAttachByAno(int ano) throws SQLException {
		attachDAO.deleteAttach(ano);

	}

}
