package com.emailtohl.hjk.crm.invoice;

import org.springframework.stereotype.Repository;

import com.emailtohl.hjk.crm.entities.Invoice;
import com.github.emailtohl.lib.jpa.AuditedRepository;

@Repository
class InvoiceRepoImpl extends AuditedRepository<Invoice, Long> implements InvoiceRepoCust {

}
