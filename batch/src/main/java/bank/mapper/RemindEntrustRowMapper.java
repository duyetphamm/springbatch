package bank.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import bank.dtos.EntrustCustomer;

public class RemindEntrustRowMapper implements RowMapper<List<EntrustCustomer>> {
	public List<EntrustCustomer> mapRow(ResultSet rs, int rowNum) throws SQLException {
		List<EntrustCustomer> list = new ArrayList<EntrustCustomer>();
		while (rs.next()) {
			EntrustCustomer s = new EntrustCustomer();
			s.setId(rs.getLong("id"));
			s.setAccount(rs.getString("account"));
			s.setBranchid(rs.getString("branchid"));
			s.setPaymentref(rs.getString("paymentref"));
			s.setTranrefnumber(rs.getString("tranrefnumber"));
			s.setPriorityorder(rs.getLong("priorityorder"));
			s.setDescription(rs.getString("description"));
			s.setRemindname(rs.getString("remindname"));
			s.setId_schedule(rs.getLong("id_schedule"));
			s.setCreaditaccount(rs.getString("creaditaccount"));
			s.setUserlegal(rs.getString("userlegal"));
			s.setAmount(rs.getBigDecimal("amount"));
			s.setType(rs.getString("type"));
			s.setUserbranchid(rs.getString("userbranchid"));
			s.setSelbranch(rs.getString("selbranch"));
			s.setLegaldate(rs.getString("legaldate"));
			s.setLegalplace(rs.getString("legalplace"));
			s.setSelcity(rs.getString("selcity"));
			s.setUserbankcode(rs.getString("userbankcode"));
			s.setUsername(rs.getString("username"));
			s.setUserdes(rs.getString("userdes"));
			s.setUseraddress(rs.getString("useraddress"));
			s.setUserbank(rs.getString("userbank"));
			s.setParcareercode(rs.getString("parcareercode"));
			s.setCo_cus_id(rs.getString("co_cus_id"));
			s.setCo_cus_short_name(rs.getString("co_cus_short_name"));
			s.setTranscycle(rs.getString("transcycle"));
			s.setCustgrpid(rs.getLong("custgrpid"));
			s.setPartner_serex_career_id(rs.getLong("partner_serex_career_id"));
			s.setDatecreate(rs.getString("datecreate"));
			s.setTown_id(rs.getString("town_id"));
			s.setPaymentinfo(rs.getString("paymentinfo"));
			s.setParcareername(rs.getString("parcareername"));
			s.setIs_email(rs.getString("is_email"));
			s.setIs_sms(rs.getString("is_sms"));
			s.setIs_preday(rs.getLong("is_preday"));
			list.add(s);
		}

		return list;
	}
}
