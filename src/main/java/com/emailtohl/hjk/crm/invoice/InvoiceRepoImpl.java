package com.emailtohl.hjk.crm.invoice;

import com.emailtohl.hjk.crm.entities.Invoice;
import com.github.emailtohl.lib.jpa.SearchInterface;
import com.github.emailtohl.lib.jpa.SearchRepository;

class InvoiceRepoImpl extends SearchRepository<Invoice, Long> implements SearchInterface<Invoice, Long> {

}
