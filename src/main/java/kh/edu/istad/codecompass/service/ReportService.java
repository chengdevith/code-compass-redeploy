package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.report.ChangeStatusRequest;
import kh.edu.istad.codecompass.dto.report.CreateReportRequest;
import kh.edu.istad.codecompass.dto.report.ReportResponse;

import java.util.List;

public interface ReportService {

    ReportResponse createReport (CreateReportRequest request);

    void changeStatus (ChangeStatusRequest request);

    List<ReportResponse> getReport();

}
