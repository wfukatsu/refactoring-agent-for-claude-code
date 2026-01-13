/* eslint-disable react/prop-types */
import "./AuditorDashboard.css";
import CalendarTodayIcon from "@mui/icons-material/CalendarToday";
import InsertDriveFileOutlinedIcon from "@mui/icons-material/InsertDriveFileOutlined";
import PersonOutlineOutlinedIcon from "@mui/icons-material/PersonOutlineOutlined";
import DescriptionOutlinedIcon from "@mui/icons-material/DescriptionOutlined";
import DataAndTime from "./DataAndTime";

import { useTranslation } from "react-i18next";

function FileDetails(props) {
  const {t,i18n} = useTranslation();
  return (
    <div className="bg-[#F9FBFE]">
      <div
        style={{
          width: "100%",
          backgroundColor: "#0061D5",
          fontSize: "20px",
          fontWeight: "700",
          borderTopLeftRadius: "16px",
          borderTopRightRadius: "16px",
          minHeight: "59px",
          color: "white",
          paddingLeft: "22px",
          paddingRight: "10px",
          paddingTop: "12px",
          paddingBottom: "12px",
        }}
      >
        {props.isfiledetails ? t("rightClickMainFilePropertiesText") : t("rightClickSecondFolderProperties")}
      </div>

      {/* <div className="flex w-full px-7 py-2 border border-b-gray-400 gap-5 justify-start items-center">
        <CalendarTodayIcon sx={{ color: "#0061D5" }} />
        <div className="felx flex-col" style={{width:"90%"}}>
          <p className="text-md">Name</p>
          <p className="text-[17px]  font-bold" style={{overflowY: "auto", wordWrap: "break-word"}}>{props.obj.name}</p>
        </div>
      </div> */}
      <div className="flex w-full px-7 py-2 border border-b-gray-400 gap-5 justify-start items-center">
        <PersonOutlineOutlinedIcon sx={{ color: "#0061D5" }} />
        <div className="felx flex-col">
          <p className="text-md">{t("rightClickMainOwnedByText")}</p>
          <p className="text-[17px]  font-bold"> {props.obj.ownedBy?.name}</p>
        </div>
      </div>
      <div className="flex w-full px-7 py-2 border border-b-gray-400 gap-5 justify-start items-center">
        <CalendarTodayIcon sx={{ color: "#0061D5" }} />
        <div className="felx flex-col" style={{ width: "90%" }}>
          <p className="text-md">{t("rightClickMainCreatedAtText")}</p>
          <p
            className="text-[17px]  font-bold"
            style={{ overflowY: "auto", wordWrap: "break-word" }}
          >
            <DataAndTime dataAndTime={props.obj.createdAt} />
            {/* {convertUtcToLocal(props.obj.createdAt)} */}
          </p>
        </div>
      </div>
      <div className="flex w-full px-7 py-2 border border-b-gray-400 gap-5 justify-start items-center">
        <PersonOutlineOutlinedIcon sx={{ color: "#0061D5" }} />
        <div className="felx flex-col">
          <p className="text-md ">{t("rightClickMainModifiedByText")}</p>
          <p className="text-[17px]  font-bold">{props.obj.modifiedBy?.name}</p>
        </div>
      </div>
      <div className="flex w-full px-7 py-2 border border-b-gray-400 gap-5 justify-start items-center">
        <CalendarTodayIcon sx={{ color: "#0061D5" }} />
        <div className="felx flex-col" style={{ width: "90%" }}>
          <p className="text-md">{t("rightClickMainModifiedAtText")}</p>
          <p
            className="text-[17px] font-bold"
            style={{ overflowY: "auto", wordWrap: "break-word" }}
          >
            <DataAndTime dataAndTime={props.obj.modifiedAt} />

            {/* {convertUtcToLocal(props.obj.modifiedAt)} */}
          </p>
        </div>
      </div>

      <div className="flex w-full px-7 py-2 border border-b-gray-400 gap-5 justify-start items-center">
        <DescriptionOutlinedIcon sx={{ color: "#0061D5" }} />
        <div className="felx flex-col">
          <p className="text-md">{t("rightClickMainSizeText")}</p>
          <p className="text-[17px]  font-bold"> {props.obj.size} </p>
        </div>
      </div>

      {props.isfiledetails && (
        <div className="flex w-full px-7 rounded-bl-xl rounded-br-xl py-2 border  border-b-gray-400 gap-5 justify-start items-center">
          <InsertDriveFileOutlinedIcon sx={{ color: "#0061D5" }} />
          <div className="felx flex-col " style={{ width: "90%" }}>
            <p className="text-md">{t("rightClickMainShaHash")}</p>
            <p
              className="text-[17px]  font-bold"
              style={{ overflowY: "auto", wordWrap: "break-word" }}
            >
              {" "}
              {props.obj.sha1}
            </p>
          </div>
        </div>
      )}
    </div>
  );
}

export default FileDetails;
