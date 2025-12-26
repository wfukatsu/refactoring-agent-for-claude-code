/* eslint-disable react/prop-types */
import { Popover } from "@mui/material";
import DropDown from "../../components/DropDown";
import { useDispatch, useSelector } from "react-redux";
import { useState } from "react";
import Header from "../../components/Header";
import FileDetails from "../../components/FileDetails";
import { BASE_URL } from "../../utils/constant";
import AddButton from "./AddButton";
import { toggleLoading, updateDenaiList } from "./auditFolderSlice";
import "./FolderAuditorDashboard.css";
import Divider from "../../components/Divider";
import NodeBottomUp from "./NodeBottomUp";
import SuccessPopUp from "../../components/SuccessPopUp";
import PathToolTip from "../../components/FileDetailsComponet";
import ErrorPopup from "../../components/ErrorPopup";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";
import { useTranslation } from "react-i18next";

function compareLists(list1, list2) {
  console.log("COMPARE LIST! :: ", list1, list2);
  if (list1.length !== list2.length) {
    return false;
  }

  list1.sort((a, b) => a.itemId - b.itemId);
  list2.sort((a, b) => a.itemId - b.itemId);

  for (let i = 0; i < list1.length; i++) {
    if (list1[i].id !== list2[i].id) {
      return false;
    }
  }

  return true;
}

export default function FolderAuditorDashboard({ itemdata }) {
  const { user } = useSelector((store) => store.auth);
  const [openSuccessDialog, setopenSuccessDialog] = useState(false);
  const [openErrorDialog, setOpenErrorDialog] = useState(false);
  const axiosPrivate = useAxiosPrivate();

  const [message, setMessage] = useState("");
  const { itemDetails } = { ...user };

  const [anchorEl, setAnchorEl] = useState(null);
  const { t, i18n } = useTranslation();

  const { allowedList, selectedAuditSet, orgDeniedList } = useSelector(
    (store) => store.folder
  );
  // const allowedListMap = convertListToMap(allowedList);
  const allowedListMap = new Map();
  // console.log("ALLOWED*** counter START", counter + 100, allowedListMap);

  [...allowedList].forEach((item) => {
    allowedListMap.set(item.id, item);
  });

  const isRootAllowed = allowedListMap.has(itemdata.id);

  // const isRootAllowed = isObjectPresent(deniedSet, itemdata.id);
  console.log("HAS ROOT :: ", isRootAllowed, itemdata.id);
  const subChild = (subChildSet) => {
    console.log("ROOT ROOT ROOT ::", subChildSet);
  };

  const removeRootCallBackFun = (removeMap) => {};

  const handlePopoverOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handlePopoverClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);
  const dispatch = useDispatch();

  const isSame = compareLists([...orgDeniedList], [...allowedList]);

  const hasChange = selectedAuditSet !== "null" && !isSame;

  function addHandler() {
    dispatch(toggleLoading());
    let filteredData = allowedList.map((item) => {
      return { id: item.id, type: item.type };
    });

    let body = {
      itemId: itemdata.id,
      itemType: itemdata.type,
      itemName: itemdata.name,
      accessListType: "ALLOWED",
      items: [...filteredData],
    };

    // const headers = new Headers();
    // headers.append("Content-Type", "application/json");
    // headers.append(
    //   "Authorization",
    //   `Bearer ${encodeURIComponent(user.jwtToken)}`
    // );
    const url = `${BASE_URL}/box/auditSetItem/addItemToAuditSet/${selectedAuditSet}`;

    const addToAuditSet = async () => {
      axiosPrivate
        .post(url, body)
        .then((response) => {
          console.log("AXIOS :: ", response);
          if (response.status === 200) {
            setMessage(response.data.message);

            let allowedList = [...response.data.data];
            dispatch(updateDenaiList(allowedList));
            setopenSuccessDialog(true);
          } else {
            setMessage(response.data.message);
            setOpenErrorDialog(true);
          }
        })
        .catch((error) => {
          console.log("AXIOS ERROR :: ", error);
          setMessage(error.response.data.message);
          setOpenErrorDialog(true);
        })
        .finally(() => {
          dispatch(toggleLoading());
        });
      // const response = await fetch(url, {
      //   method: "POST",
      //   headers: headers,
      //   body: JSON.stringify(body),
      // });

      // if (response.status === 400) {
      //   const jsonData = await response.json();
      //   setMessage(jsonData.message);
      //   setOpenErrorDialog(true);
      // } else if (response.ok) {
      //   const jsonData = await response.json();
      //   setMessage(jsonData.message);

      //   let allowedList = [...jsonData.data];
      //   dispatch(updateDenaiList(allowedList));
      //   setopenSuccessDialog(true);
      // } else {
      //   throw Error("Something went wrong ");
      // }
    };

    addToAuditSet().catch((error) => {
      // console.log("ERROR :: ", error);
    });
  }

  return (
    <div className="w-full">
      <Header />
      <div className="flex gap-10 pl-6 pb-2 pt-2">
        <PathToolTip
          text={itemDetails.name}
          title={t("rightClickSecondFolderNameText")}
        />
        {/* <h1 className="text-xl text-black bg-white font-bold">
          Folder Name : {itemdata.name}
        </h1> */}
        {/* <SCLable title={"Folder Path"} name={itemDetails.path} /> */}
        <PathToolTip
          text={itemDetails.path}
          title={t("rightClickSecondFolderpathText")}
        />
      </div>
      <div className="main px-5">
        <div className="bg-[#F9FBFE] border rounded-tr-2xl rounded-br-xl rounded-tl-2xl  border-gray-300  rounded-bl-xl w-[80%]">
          {/* <h1 style={{ padding: "20px" }}>Add to Audit Set</h1>
           */}
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
              padding: "14px 20px",
            }}
          >
            {t("rightClickSecondFolderAddAuditText")}
          </div>
          <DropDown itemId={itemdata.id} />
          <Divider />

          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              padding: "5px 20px",
            }}
          >
            <h2
              style={{
                fontSize: "16px",
                fontWeight: "700",
                color: "rgba(0, 0, 0, 1)",
              }}
            >
              {t("rightClickSecondFolderAllowedText")}
            </h2>
          </div>
          <Divider />

          <div className="flex min-h-[350px] px-4 py-4">
            <NodeBottomUp
              item={itemdata}
              setSubChild={subChild}
              isRootAllowed={isRootAllowed}
              isRoot={true}
              removeRoot={false}
              parentItem={itemdata}
              rootsItems={[itemdata]}
              // removeRootMap={new Map()}
              removeRootCallBack={removeRootCallBackFun}
            />
            {/* <RootTest
              item={itemdata}
              setSubChild={subChild}
              isRootAllowed={isRootAllowed}
              isRoot={true}
              removeRoot={false}
              parentItem={itemdata}
            /> */}

            {selectedAuditSet === "null" && (
              <Popover
                id="mouse-over-popover"
                sx={{
                  pointerEvents: "none",
                }}
                open={open}
                anchorEl={anchorEl}
                anchorOrigin={{
                  vertical: "bottom",
                  horizontal: "left",
                }}
                transformOrigin={{
                  vertical: "top",
                  horizontal: "left",
                }}
                onClose={handlePopoverClose}
                disableRestoreFocus
              >
                <h2 style={{ padding: "5px 10px" }}>Please select Audit Set</h2>
              </Popover>
            )}
          </div>
          <Divider />
          <AddButton
            hasChange={hasChange}
            addHandler={addHandler}
            handlePopoverOpen={handlePopoverOpen}
            handlePopoverClose={handlePopoverClose}
          />
        </div>
        <div className="w-[25%]">
          <FileDetails obj={itemDetails} isfiledetails={false} />
        </div>
      </div>
      {openSuccessDialog && (
        <SuccessPopUp
          iconColor="primary"
          iconSize={50}
          title={message}
          open={true}
          handleClose={() => {
            setopenSuccessDialog(false);
            setMessage("");
          }}
        />
      )}
      {openErrorDialog && (
        <ErrorPopup
          iconColor="error"
          iconSize={50}
          title={message}
          open={true}
          handleClose={() => {
            setOpenErrorDialog(false);
            setMessage("");
          }}
        />
      )}
    </div>
  );
}
