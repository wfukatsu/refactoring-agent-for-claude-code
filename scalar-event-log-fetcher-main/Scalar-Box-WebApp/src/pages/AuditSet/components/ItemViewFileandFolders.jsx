import React, { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate";
import "./itemViewFileandFolders.css";
import { useParams } from "react-router-dom";
import Node from "./Node";
import { Divider, Paper } from "@mui/material";
import FolderIcon from "@mui/icons-material/Folder";
import DescriptionIcon from "@mui/icons-material/Description";
import { blue } from "@mui/material/colors";
import { BASE_URL } from "../../../utils/constants";
import CircularProgress from "@mui/material/CircularProgress";

import DataAndTime from "../../../common/DataAndTime";
import { reset } from "../../../redux/reducerSlice/folderAndFileSlice";
import PathToolTip from "../../../common/FileInfoComponent/PathToolTip";

import { useTranslation } from "react-i18next";
// export const SET_LOADING = 'auth/SET_LOADING';

const ItemViewFileandFolders = ({ open }) => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [rootFiles, setRootFiles] = useState([]);
  const [selectedItem, setSelectedItem] = useState(null);
  const { auditSetId, auditSetName } = useParams();
  const [showFullItemName, setShowFullItemName] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const dispatch = useDispatch();
  const axiosPrivate = useAxiosPrivate();

  const { t, i18n } = useTranslation();

  const toggleFullName = () => {
    setShowFullItemName(!showFullItemName);
  };

  const fetchData = (auditSetId) => {
    if (!isLoaded) {
      dispatch(reset());
      setIsLoading(true); // Set loading to true when fetching data

      axiosPrivate
        .get(
          `${BASE_URL}/box/auditSetItem/viewItemsFromSelectedAuditSet/${auditSetId}`
        )
        .then((response) => {
          if (response.data.status) {
            const data = response.data.data;
            setRootFiles(data);
            if (data.length > 0) {
              setSelectedItem(data[0]);
            }
            setIsLoaded(false);
          } else {
            console.error(response.data.message);
          }
        })
        .catch((error) => {
          setIsLoading(false); // Set loading to false on error
          console.error(error);
        })
        .finally(() => {
          setIsLoading(false); // Set loading to false regardless of success or failure
        });
    }
  };

  useEffect(() => {
    if (auditSetId) {
      fetchData(auditSetId);
    }

    return () => {
      dispatch(reset());
    };
  }, [auditSetId]);

  return (
    // <Main open={open}>
    <div className="flex flex-col lg:flex-row p-1 ">
      {isLoading && (
        <div className="loading-overlay">
          <CircularProgress className="loading-spinner" />
        </div>
      )}
      <div className="flex flex-col border w-full lg:w-3/4 border-blue-500 rounded-xl">
        <div className="bg-blue-500 p-3 text-white font-bold rounded-t-xl">
          {/* <h1>{t("viewItemsUnderAuditScreenSubTitleText")}</h1> */}
          {/* <h1>{t("itemsInText")} {auditSetName}</h1> */}
          {i18n.language === "ja" ? (
            <h1>
              {auditSetName} {t("itemsInText")}
            </h1>
          ) : (
            <h1>
              {t("itemsInText")} {auditSetName}
            </h1>
          )}
        </div>
        <div className="mb-2  ">
          <div className="w-full flex justify-between py-1 border px-4">
            <p className="font-bold px-3.5">
              {t("viewItemsUnderAuditScreenSubNameText")}
            </p>
            <div
              style={{
                display: "flex",
                gap: i18n.language === "ja" ? "6rem" : "4rem",
              }}
            >
              <p className="font-bold">
                {t("viewItemsUnderAuditScreenSubModifiedAtText")}
              </p>
              <p className="font-bold">
                {t("viewItemsUnderAuditScreenSubSize")}
              </p>
              <p className="font-bold">
                {t("viewItemsUnderAuditScreenSubModifiedByText")}
              </p>
            </div>
          </div>
          {rootFiles.length === 0 && !isLoading ? (
            <div
              style={{
                paddingTop: "30px",
                justifyContent: "center",
                height: "100%",
              }}
            >
              <p
                style={{
                  textAlign: "center",
                }}
              >
                {t("noFilesAvaailbeText")}
              </p>
            </div>
          ) : (
            rootFiles.map((item) => (
              <Node
                key={item.itemId}
                item={item}
                auditSetId={auditSetId}
                rootId={item.itemId}
                setSelectedItem={setSelectedItem}
              />
            ))
          )}
        </div>
      </div>
      <div style={{ width: "10px" }}></div>
      <Paper className="flex flex-col  justify-between border w-1/2 lg:w-1/3 rounded-xl h-fit">
        {selectedItem && (
          <div className="flex flex-col justify-between w-full  ">
            {/* <div>
                <PreviewFile selectedItem={selectedItem} />
              </div> */}

            {/* <div>
              {selectedItem.itemType === "file" && (
                <PreviewFile selectedItem={selectedItem} />
              )}
            </div> */}

            <div className="p-3">
              <div className="flex justify-between mb-1 ">
                <h1 className="text-md font-bold gap-2 flex justify-center items-center cursor-pointer">
                  {selectedItem.itemType === "file" ? (
                    <DescriptionIcon sx={{ color: blue[700] }} />
                  ) : (
                    <FolderIcon sx={{ color: blue[700] }} />
                  )}
                  <PathToolTip text={selectedItem.itemName} />
                </h1>
              </div>
              <Divider variant="fullWidth" color="#fff" />
              <h1 className="text-md font-bold py-1">
                {t("viewItemsUnderAuditScreenSubFileDetailsText")}
              </h1>

              <div className="flex justify-between gap-1">
                <p className="text-sm font-bold">
                  {t("viewItemsUnderAuditScreenSubCreatedAtText")}
                </p>

                <DataAndTime
                  dataAndTime={selectedItem.createdAt}
                  fontSize="14px"
                />
              </div>
              <div className="flex justify-between gap-1">
                <p className="text-sm font-bold">
                  {t("viewItemsUnderAuditScreenSubModifiedAtText")}
                </p>

                <DataAndTime
                  dataAndTime={selectedItem.modifiedAt}
                  fontSize="14px"
                />
              </div>
              <div className="flex justify-between gap-1">
                <p className="text-sm font-bold">
                  {t("viewItemsUnderAuditScreenSubCreatdByText")}
                </p>
                <p className="text-[14px]">{selectedItem.createdBy}</p>
              </div>
              <div className="flex justify-between gap-1 items-center">
                <p className="text-sm font-bold">
                  {t("viewItemsUnderAuditScreenSubModifiedByText")}
                </p>
                <p className="text-[14px]">{selectedItem.modifiedBy}</p>
              </div>
              <div className="flex justify-between gap-1 items-center">
                <p className="text-sm font-bold">
                  {t("viewItemsUnderAuditScreenSubSize")}
                </p>
                <p className="text-[14px]">{selectedItem.size}</p>
              </div>
              {selectedItem.itemType !== "file" ? (
                <div></div>
              ) : (
                <div className="flex justify-between gap-1 items-start">
                  <p className="text-sm font-bold">
                    {t("rightClickMainShaHash")}
                  </p>
                  <div style={{ width: "50%" }}>
                    <p
                      style={{ overflowWrap: "break-word" }}
                      className="text-[14px]"
                    >
                      {selectedItem.sha1Hash}
                    </p>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}

        {!selectedItem && (
          <div className="flex flex-col justify-center items-center">
            <FolderIcon color="primary" sx={{ fontSize: "100px" }} />
            <h1 className="text-xl font-bold">
              {t("viewItemsUnderAuditScreenSubFileDetailsText")}
            </h1>
          </div>
        )}
      </Paper>
    </div>
    // </Main>
  );
};

export default ItemViewFileandFolders;
