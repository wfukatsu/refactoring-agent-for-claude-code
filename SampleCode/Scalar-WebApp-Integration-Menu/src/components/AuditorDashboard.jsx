import React, { useEffect, useState } from "react";
import Header from "./Header";
import "./AuditorDashboard.css";
import FileDetails from "./FileDetails";
// import { getFileCopy, getVersionHistory } from "./ExternalServices";
import ScrollDialog from "./ScrollDialog";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

import { styled } from "@mui/material/styles";
import Paper from "@mui/material/Paper";
import TableCell, { tableCellClasses } from "@mui/material/TableCell";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";

import PathToolTip from "./FileDetailsComponet";
import { BASE_URL } from "../utils/constant";
import Loader from "./Loader";
import DataAndTime from "./DataAndTime";
import useAxiosPrivate from "../hooks/useAxiosPrivate";
import { useTranslation } from "react-i18next";

const StyledTableCell = styled(TableCell)(({ theme }) => ({
  [`&.${tableCellClasses.head}`]: {
    fontSize: 16,
    backgroundColor: "#0061D5",
    color: theme.palette.common.white,
    maxHeight: 20,
  },
  [`&.${tableCellClasses.body}`]: {
    fontSize: 14,
    alignItems: "left",
    // height: 50,
  },
}));



function AuditorDashboard() {
  

  const [page, setPage] = React.useState(0);
  const [rowsPerPage, setRowsPerPage] = React.useState(10);
  const axiosPrivate = useAxiosPrivate();
  const {t,i18n} = useTranslation();

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  const columns = [
    { id: "version", label: t("rightClickMainVersionHVersionText"), minWidth: 150, align: "left" },
    { id: "updatedat", label: t("rightClickMainVersionHUpdatedAtText"), minWidth: 150, align: "left" },
    { id: "hash", label: t("rightClickMainVersionHHashText"), minWidth: 150, align: "left" },
  ];
  
  const columns2 = [
    { id: "Filename", label: t("rightClickMainFileNameText"), minWidth: 150, align: "left" },
    { id: "Created", label: t("rightClickMainCreatedAtText"), minWidth: 150, align: "left" },
    { id: "path", label: t("rightClickMainFileCopiesPathText"), minWidth: 120, align: "left" },
  ];

  const navigate = useNavigate();
  const { user } = useSelector((store) => store.auth);

  const { itemDetails } = { ...user };
  const { id, sha1 } = { ...itemDetails };
  console.log("TOKEN :: ", user);

  const [isVersionHistorySelected, setIsVersionHistorySelected] =
    useState(true);

  const [versionHistoryData, setVersionHistoryData] = useState([]);
  const [fileCopyData, setFileCopyData] = useState([]);

  const [isLoading, setIsloading] = useState(false);

  async function check() {
    setIsloading(true);
    const fileVersionUrl = `${BASE_URL}/box/file/getFileVersions?fileId=${id}`;
    const response = await axiosPrivate.get(fileVersionUrl);
    // const response = await getVersionHistory(id, user.jwtToken);
    if (response.status === 200) {
      // const data = await response.json();
      setVersionHistoryData(response.data.data);
    } else {
      console.log("Error fetching VersionHistory data", response);
    }

    const fileCopiesUrl = `${BASE_URL}/box/file/getFileCopies?sha1Hash=${sha1}&itemId=${id}`;
    const response1 = await axiosPrivate.get(fileCopiesUrl);
    // const response1 = await getFileCopy(id, sha1, user.jwtToken);
    if (response1.status === 200) {
      // const data1 = await response1.json();
      setFileCopyData(response1.data.data);
    } else {
      console.log("Error fetching VersionHistory data", response);
    }
    setIsloading(false);
  }

  useEffect(() => {
    check();
  }, []);

  return (
    <>
      <Header />

      <div style={{ height: "100%", width: "100%" }}>
        <div
          style={{
            height: "10%",
            display: "flex",
            flexDirection: "row",
            justifyContent: "space-between",
            paddingTop: "10px",
          }}
        >
          <div
            style={{
              width: "100%",
              height: "50%",
              display: "flex",
              flexDirection: "column",
            }}
          >
            <div
              style={{
                paddingLeft: "25px",
                display: "flex",
                flexDirection: "row",
                width: "100%",
                justifyContent: "start",
                alignItems: "center", // Align items to center vertically
              }}
            >
              {/* <NoMaxWidthTooltip title={itemDetails.name}>
              <p
                style={{ fontSize: "18px", marginRight: "10px" }} // Add margin to create space between elements
                onMouseEnter={handleMouseEnter}
                onMouseLeave={handleMouseLeave}
              >
                <b>File Name :</b>{" "}
                {showText || itemDetails.name.length <= 20
                  ? itemDetails.name.substring(0, 20)
                  : itemDetails.name.substring(0, 20) + "..."}
              </p>
              </NoMaxWidthTooltip> */}
              <PathToolTip text={itemDetails.name} title={t("rightClickMainFileNameText")} />
              <div style={{ width: "10%" }}></div>
              <PathToolTip text={itemDetails.path} title={t("rightClickMainFilePathText")} />
            </div>

            <div
              style={{
                height: "50%",
                display: "flex",
                flexDirection: "row",
                fontSize: "18px",
              }}
              className="bg-white text-xl flex justify-start gap-5 pl-3 ml-3"
            >
              <b>{t("rightClickMainFileStatusText")} :</b>{" "}
              <div className="text-blue-600 text   justify-items-center justify-center rounded-xl text-center">
                <b>
                  <span
                    style={{
                      backgroundColor: "lightgray",
                      padding: "0px 8px",
                      borderRadius: "5px",
                      fontSize: "15px",
                    }}
                  >
                    {itemDetails.tamperedStatus}
                  </span>{" "}
                </b>
              </div>
            </div>
          </div>

          <div className="bg-white flex justify-between ml-3 pt-1">
            <div
              className="bg-white justify-start gap-1"
              style={{ display: "flex", flexDirection: "row" }}
            >
              <ScrollDialog reduxdata={itemDetails}></ScrollDialog>
              <button
                id="btn2"
                style={{
                  width: "212px",
                  height: "45px",
                  borderRadius: "100px",
                  padding: "10px, 10px, 10px, 10px",
                  border: "1px solid #0061D5",
                  fontSize: 18,
                }}
                onMouseEnter={(e) =>
                  (e.target.style.backgroundColor = "lightgray")
                }
                onMouseLeave={(e) => (e.target.style.backgroundColor = "white")}
                onClick={() => navigate("/event-history")}
              >
                {t("rightClickMainViewEventHistory")}
              </button>
              <div style={{ width: "10px" }}></div>
              {/* <div style={{ width: "25px" }}></div> */}
            </div>
          </div>
        </div>

        {/* <div style={{height:"2.5%",width:"100%",backgroundColor:"white"}}>d</div> */}
        <div style={{ height: "5px" }}></div>
        <div style={{ height: "65%", width: "100%" }}>
          {/*table selection Buttons */}
          <div>
            <div className="bg-white gap-x-8 px-3  ml-2 mb-1 mt-1">
              <div
                style={{
                  backgroundColor: "#D9D9D9",
                  width: "555px",
                  display: "flex",
                  flexDirection: "row",
                  height: "8%",
                  fontSize: 18,

                  borderTopLeftRadius: "10px",
                }}
              >
                <button
                  className={`btn ${
                    isVersionHistorySelected ? "button1" : "default"
                  }`}
                  onClick={() => {
                    setPage(0);
                    setIsVersionHistorySelected(true);
                  }}
                  style={{
                    width: "278px",
                    height: "40px",
                    backgroundColor: isVersionHistorySelected
                      ? "#0061D5"
                      : "#e0ecfa",
                  }}
                >
                  {t("rightClickMainVersionHistoryButtonText")}
                </button>
                <button
                  className={`btn ${
                    !isVersionHistorySelected ? "button2" : "default"
                  }`}
                  onClick={() => {
                    setPage(0);
                    setIsVersionHistorySelected(false);
                  }}
                  style={{
                    width: "278px",
                    height: "40px",
                    backgroundColor: !isVersionHistorySelected
                      ? "#0061D5"
                      : "#e0ecfa", // Change background color conditionally
                  }}
                >
                  {" "}
                  {t("rightClickMainFileCopiesButtonText")}{" "}
                </button>
              </div>
            </div>
          </div>
          <div
            style={{
              backgroundColor: "white",
              marginLeft: "14px",
              display: "flex",
              flexDirection: "row",
              gap: "20px",
              flexWrap: "wrap",
              //minHeight: "660px",
              height: "90%",
            }}
          >
            <div
              style={{
                marginLeft: "5px",
                width: "70%",
                // height: "650px",
                backgroundColor: "rgba(204,223,247,0.12)",
              }}
            >
              {isVersionHistorySelected && (
                <Paper
                  style={{
                    width: "100%",
                    overflow: "hidden",
                    borderRadius: "25px",
                    backgroundColor: "#F9FBFE",
                  }}
                >
                  {isLoading ? (
                    <>
                      <TableContainer
                        // style={{
                        //   width: "100%",
                        //   overflow: "hidden",
                        // }}
                        sx={{ height:"440px" }}
                      >
                        <Table stickyHeader aria-label="sticky table">
                          <TableHead>
                            <TableRow>
                              {columns.map((column) => (
                                <StyledTableCell
                                  key={column.id}
                                  align={"left"}
                                  style={{
                                    minWidth: column.minWidth,
                                    paddingLeft: "70px",
                                  }}
                                >
                                  {column.label}
                                </StyledTableCell>
                              ))}
                            </TableRow>
                          </TableHead>
                          <TableBody>
                            <TableRow>
                              <TableCell colSpan={columns2.length}>
                                <div
                                  style={{
                                    display: "flex",
                                    justifyContent: "center",
                                    alignItems: "center",
                                    height:"440px"
                                  }}
                                >
                                  <p className="text-xl font-bold">
                                    <Loader color="#0061D5" />
                                  </p>
                                </div>
                              </TableCell>
                            </TableRow>
                          </TableBody>
                        </Table>
                      </TableContainer>
                      <TablePagination
                        rowsPerPageOptions={[5, 10, 25]}
                        component="div"
                        count={versionHistoryData.length}
                        rowsPerPage={rowsPerPage}
                        labelRowsPerPage={t("tablePaginationTitleText")}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                        labelDisplayedRows={({ from, to, count }) => {
                          if(i18n.language == "ja"){
                            return `全アイテム  ${count} ${t("tablePageOffText")} ${from}-${to}  (${t("tablePageText")}  ${Math.ceil(count / rowsPerPage)})`;
                          }

                          return `${from}-${to} ${t("tablePageOffText")} ${count} (${Math.ceil(
                            count / rowsPerPage
                          )} ${t("tablePageText")})`;
                        }}
                      />
                    </>
                  ) : versionHistoryData.length === 0 ? (
                    <>
                      <TableContainer
                        style={{
                          width: "100%",
                          overflow: "hidden",
                        }}
                      >
                        <Table stickyHeader aria-label="sticky table">
                          <TableHead>
                            <TableRow>
                              {columns.map((column) => (
                                <StyledTableCell
                                  key={column.id}
                                  align={"left"}
                                  style={{
                                    minWidth: column.minWidth,
                                    paddingLeft: "70px",
                                  }}
                                >
                                  {column.label}
                                </StyledTableCell>
                              ))}
                            </TableRow>
                          </TableHead>
                          <TableBody>
                            <TableRow>
                              <TableCell colSpan={columns2.length}>
                                <div
                                  style={{
                                    display: "flex",
                                    justifyContent: "center",
                                    alignItems: "center",
                                    minHeight: "440px", // Adjust height as needed
                                  }}
                                >
                                  <p className="text-xl font-bold">
                                    {t("rightClickMainVersionEmptyText")}
                                  </p>
                                </div>
                              </TableCell>
                            </TableRow>
                          </TableBody>
                        </Table>
                      </TableContainer>
                    </>
                  ) : (
                    <>
                      <TableContainer style={{ maxHeight: 440 }}>
                        <Table stickyHeader aria-label="sticky table">
                          <TableHead>
                            <TableRow>
                              {columns.map((column) => (
                                <StyledTableCell
                                  key={column.id}
                                  align={"left"}
                                  style={{
                                    minWidth: column.minWidth,
                                    paddingLeft: "70px",
                                  }}
                                >
                                  {column.label}
                                </StyledTableCell>
                              ))}
                            </TableRow>
                          </TableHead>
                          <TableBody>
                            {versionHistoryData &&
                              versionHistoryData
                                .slice(
                                  page * rowsPerPage,
                                  page * rowsPerPage + rowsPerPage
                                )
                                .map((item, index) => (
                                  <TableRow key={index}>
                                    <TableCell
                                      align="left"
                                      style={{ paddingLeft: "70px" }}
                                    >
                                      V{item.itemVersionNumber}
                                    </TableCell>
                                    <TableCell
                                      align="left"
                                      style={{ paddingLeft: "70px" }}
                                    >
                                      <DataAndTime
                                        dataAndTime={item.modifiedAt}
                                      />
                                      {/* {convertUtcToLocal(item.modifiedAt)} */}
                                    </TableCell>
                                    <TableCell
                                      align="left"
                                      style={{ paddingLeft: "70px" }}
                                    >
                                      {item.sha1Hash}
                                    </TableCell>
                                  </TableRow>
                                ))}
                          </TableBody>
                        </Table>
                      </TableContainer>
                      <TablePagination
                        rowsPerPageOptions={[5, 10, 25]}
                        component="div"
                        count={versionHistoryData.length}
                        rowsPerPage={rowsPerPage}
                        labelRowsPerPage={t("tablePaginationTitleText")}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                        labelDisplayedRows={({ from, to, count }) => {
                          if(i18n.language == "ja"){
                            return `全アイテム  ${count} ${t("tablePageOffText")} ${from}-${to}  (${t("tablePageText")}  ${Math.ceil(count / rowsPerPage)})`;
                          }

                          return `${from}-${to} ${t("tablePageOffText")} ${count} (${Math.ceil(
                            count / rowsPerPage
                          )} ${t("tablePageText")})`;
                        }}
                      />
                    </>
                  )}
                </Paper>
              )}

              {!isVersionHistorySelected && (
                <Paper
                  style={{
                    width: "100%",
                    overflow: "hidden",
                    borderRadius: "25px",
                    backgroundColor: "#F9FBFE",
                    // height: "480px",
                  }}
                >
                  {isLoading ? (
                    <>
                      <TableContainer
                        // style={{
                        //   width: "100%",
                        //   overflow: "hidden",
                        // }}
                        sx={{ maxHeight: 440 }}
                      >
                        <Table stickyHeader aria-label="sticky table">
                          <TableHead>
                            <TableRow>
                              {columns2.map((column) => (
                                <StyledTableCell
                                  key={column.id}
                                  align={"left"}
                                  style={{
                                    width: column.minWidth,
                                    paddingLeft: "70px",
                                  }}
                                >
                                  {column.label}
                                </StyledTableCell>
                              ))}
                            </TableRow>
                          </TableHead>
                          <TableBody>
                            <TableRow>
                              <TableCell colSpan={columns2.length}>
                                <div
                                  style={{
                                    display: "flex",
                                    justifyContent: "center",
                                    alignItems: "center",
                                    height:"400px"
                                  }}
                                >
                                  <p className="text-xl font-bold">
                                    <Loader color="#0061D5" />
                                  </p>
                                </div>
                              </TableCell>
                            </TableRow>
                          </TableBody>
                        </Table>
                      </TableContainer>
                    </>
                  ) : fileCopyData.length === 0 ? (
                    <>
                      <TableContainer
                        style={{
                          width: "100%",
                          overflow: "hidden",
                        }}
                      >
                        <Table stickyHeader aria-label="sticky table">
                          <TableHead>
                            <TableRow>
                              {columns2.map((column) => (
                                <StyledTableCell
                                  key={column.id}
                                  align={"left"}
                                  style={{
                                    minWidth: column.minWidth,
                                    paddingLeft: "70px",
                                  }}
                                >
                                  {column.label}
                                </StyledTableCell>
                              ))}
                            </TableRow>
                          </TableHead>
                          <TableBody>
                            <TableRow>
                              <TableCell colSpan={columns2.length}>
                                <div
                                  style={{
                                    display: "flex",
                                    justifyContent: "center",
                                    alignItems: "center",
                                    minHeight: "400px", // Adjust height as needed
                                  }}
                                >
                                  <p className="text-xl font-bold">
                                    {t("rightClickMainFileCopiesEmptyText")}
                                  </p>
                                </div>
                              </TableCell>
                            </TableRow>
                          </TableBody>
                        </Table>
                      </TableContainer>
                    </>
                  ) : (
                    <TableContainer
                      // style={{
                      //   width: "100%",
                      //   overflow: "hidden",
                      // }}
                      sx={{ maxHeight: 440 }}
                    >
                      <Table stickyHeader aria-label="sticky table">
                        <TableHead>
                          <TableRow>
                            {columns2.map((column) => (
                              <StyledTableCell
                                key={column.id}
                                align={"left"}
                                style={{
                                  width: column.minWidth,
                                  paddingLeft: "70px",
                                }}
                              >
                                {column.label}
                              </StyledTableCell>
                            ))}
                          </TableRow>
                        </TableHead>
                        <TableBody>
                          {fileCopyData &&
                            fileCopyData
                              .slice(
                                page * rowsPerPage,
                                page * rowsPerPage + rowsPerPage
                              )
                              .map((item, index) => (
                                <TableRow key={index}>
                                  <TableCell
                                    align="left"
                                    style={{ paddingLeft: "70px" }}
                                  >
                                    {item.itemName}
                                    {item.isDeleted && (
                                      <span
                                        style={{
                                          marginLeft: "10px",
                                          borderRadius: "5px",
                                          paddingTop: "2px",
                                          paddingBottom: "2px",
                                          paddingRight: "5px",
                                          paddingLeft: "5px",
                                          backgroundColor: "#E0E0E0",
                                          color: "black",
                                        }}
                                      >
                                        deleted
                                      </span>
                                    )}
                                  </TableCell>
                                  <TableCell
                                    align="left"
                                    style={{ paddingLeft: "70px" }}
                                  >
                                    <DataAndTime dataAndTime={item.createdAt} />
                                    {/* {convertUtcToLocal(item.createdAt)} */}
                                  </TableCell>
                                  <TableCell
                                    align="left"
                                    style={{ paddingLeft: "70px" }}
                                  >
                                    <PathToolTip
                                      text={item.path}
                                      hideTitle={true}
                                      fontSize={14}
                                    />
                                  </TableCell>
                                </TableRow>
                              ))}
                        </TableBody>
                      </Table>
                    </TableContainer>
                  )}
                  <TablePagination
                    rowsPerPageOptions={[5, 10, 25]}
                    component="div"
                    count={fileCopyData.length}
                    rowsPerPage={rowsPerPage}
                    labelRowsPerPage={t("tablePaginationTitleText")}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                    labelDisplayedRows={({ from, to, count }) => {

                      if(i18n.language == "ja"){
                        return `全アイテム  ${count} ${t("tablePageOffText")} ${from}-${to}  (${t("tablePageText")}  ${Math.ceil(count / rowsPerPage)})`;
                      }

                      return `${from}-${to} ${t("tablePageOffText")} ${count} (${Math.ceil(
                        count / rowsPerPage
                      )} ${t("tablePageText")})`;
                    }}
                  />
                </Paper>
              )}
            </div>
            <div className="w-[27%]">
              <FileDetails obj={itemDetails} isfiledetails={true} />
              {/* <BoxPreview fileId={itemDetails.id} /> */}
            </div>
          </div>
        </div>
        <div style={{ height: "10%", width: "100%" }}></div>
      </div>
    </>
  );
}

export default AuditorDashboard;
