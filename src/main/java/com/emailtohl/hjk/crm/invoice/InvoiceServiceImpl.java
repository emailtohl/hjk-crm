package com.emailtohl.hjk.crm.invoice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.emailtohl.hjk.crm.entities.Image;
import com.emailtohl.hjk.crm.entities.Invoice;
import com.github.emailtohl.lib.StandardService;
import com.github.emailtohl.lib.jpa.Paging;
/**
 * 发票资料管理接口的实现
 * @author HeLei
 */
@Service
@Transactional
public class InvoiceServiceImpl extends StandardService<Invoice, Long, Long> implements InvoiceService {
	@Autowired
	private InvoiceRepo invoiceRepo;
	
	@Override
	public Invoice create(@Valid Invoice invoice) {
		validate(invoice);
		invoice.setApproved(false);// 创建时，设置审核未通过
		invoiceRepo.persist(invoice);
		return invoice;
	}

	@Override
	public Invoice read(Long id) {
		Invoice source = invoiceRepo.findById(id).get();
		return toTransient(source);
	}

	@Override
	public Paging<Invoice> query(Invoice example, Pageable pageable) {
		Page<Invoice> page = invoiceRepo.queryForPage(example, pageable);
		List<Invoice> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}

	@Override
	public List<Invoice> query(Invoice example) {
		return invoiceRepo.queryForList(example).stream().map(this::toTransient).collect(Collectors.toList());
	}

	@Override
	public Invoice update(Long id, Invoice invoice) {
		Invoice source = invoiceRepo.findById(id).get();
		if (hasText(invoice.getAccount())) {
			source.setAccount(invoice.getAccount());
		}
		if (hasText(invoice.getAddress())) {
			source.setAddress(invoice.getAddress());
		}
		if (hasText(invoice.getDepositBank())) {
			source.setDepositBank(invoice.getDepositBank());
		}
		if (hasText(invoice.getOrganization())) {
			source.setOrganization(invoice.getOrganization());
		}
		if (hasText(invoice.getPrincipal())) {
			source.setPrincipal(invoice.getPrincipal());
		}
		if (hasText(invoice.getTaxNumber())) {
			source.setTaxNumber(invoice.getTaxNumber());
		}
		if (hasText(invoice.getTelephone())) {
			source.setTelephone(invoice.getTelephone());
		}
		if (invoice.getType() != null) {
			source.setType(invoice.getType());
		}
		return toTransient(source);
	}

	@Override
	public void delete(Long id) {
		invoiceRepo.deleteById(id);
	}

	@Override
	public void approve(long id, boolean approve) {
		Invoice invoice = invoiceRepo.find(id);
		if (invoice != null) {
			invoice.setApproved(approve);
		}
	}

	@Override
	public Set<Image> getCredentials(Long invoiceId) {
		Invoice source = invoiceRepo.findById(invoiceId).get();
		return source.getCredentials();
	}

	@Override
	public Paging<Invoice> query(String query, Pageable pageable) {
		Page<Invoice> page = invoiceRepo.search(query, pageable);
		List<Invoice> ls = page.getContent().stream().map(this::toTransient).collect(Collectors.toList());
		return new Paging<>(ls, pageable, page.getTotalElements());
	}

	@Override
	protected Invoice toTransient(Invoice source) {
		if (source == null) {
			return source;
		}
		Invoice target = new Invoice();
		BeanUtils.copyProperties(source, target, Invoice.getIgnoreProperties("credentials"));
		return target;
	}

	@Override
	protected Invoice transientDetail(@Valid Invoice source) {
		return toTransient(source);
	}
	
}
