package com.emailtohl.hjk.crm.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emailtohl.hjk.crm.entities.Invoice;

@Repository
interface InvoiceRepo extends JpaRepository<Invoice, Long>, InvoiceRepoCust {

}
