package com.emailtohl.hjk.crm.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailtohl.hjk.crm.entities.BinFile;
import com.emailtohl.hjk.crm.file.BinFileRepo;

@RestController
@RequestMapping(value = "files", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class FileCtl {
	private static final Logger LOG = LogManager.getLogger();
	private static final FileNameMap FILE_NAME_MAP = URLConnection.getFileNameMap();
	@Autowired
	private BinFileRepo binFileRepo;
	
	@PostMapping
	public List<BinFile> save(HttpServletRequest request) {
		List<BinFile> res = new ArrayList<>();

		Collection<Part> fileParts = null;
		Map<String, String[]> map = request.getParameterMap();
		try {
			fileParts = request.getParts();
		} catch (IOException | ServletException e) {
			LOG.error("can't read part", e);
			return res;
		}
		for (Iterator<Part> iterable = fileParts.iterator(); iterable.hasNext();) {
			Part filePart = iterable.next();
			String submittedFileName = filePart.getSubmittedFileName();
			if (submittedFileName != null && !map.containsKey(submittedFileName)) {
				try (InputStream in = filePart.getInputStream();
						ByteArrayOutputStream out = new ByteArrayOutputStream()) {
					StreamUtils.copy(in, out);
					BinFile BinFile = new BinFile(submittedFileName, FILE_NAME_MAP.getContentTypeFor(submittedFileName),
							out.toByteArray());
					res.add(BinFile);
				} catch (IOException e) {
					LOG.error(submittedFileName + " file read failed", e);
				}
			}
		}
		return res;
	}

	@GetMapping(value = "{id}")
	public void download(@PathVariable("id") Long id, HttpServletResponse response) {
		BinFile BinFile = binFileRepo.findById(id).get();
		String filename;
		try {
			filename = URLEncoder.encode(BinFile.getFilename(), "UTF-8");
			response.setHeader("content-disposition", "attachment;fileName=" + filename);
		} catch (UnsupportedEncodingException e) {
			LOG.error(BinFile.getFilename() + "Failed to transcode utf-8");
		}
		response.setContentType(BinFile.getMimeType());
		try (ServletOutputStream out = response.getOutputStream()) {
			out.write(BinFile.getBin());
		} catch (IOException e) {
			LOG.error(BinFile.getFilename() + " file read failed", e);
		}
	}
}
