package com.emailtohl.hjk.crm.invoice;

import org.springframework.data.jpa.repository.JpaRepository;

import com.emailtohl.hjk.crm.entities.Invoice;

interface InvoiceRepo extends JpaRepository<Invoice, Long>, InvoiceRepoCust {
	
}
