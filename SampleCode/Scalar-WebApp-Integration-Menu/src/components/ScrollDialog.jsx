import * as React from "react";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import { Checkbox } from "@mui/material";
import { useSelector } from "react-redux";
import SuccessPopUp from "./SuccessPopUp";
import ClearIcon from "@mui/icons-material/Clear";
import ErrorPopup from "./ErrorPopup";
import { useState } from "react";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RadioButtonUncheckedIcon from "@mui/icons-material/RadioButtonUnchecked";
import { BASE_URL } from "../utils/constant";
import { LoadingButton } from "@mui/lab";
import useAxiosPrivate from "../hooks/useAxiosPrivate";

import { useTranslation } from "react-i18next";

export default function ScrollDialog(props) {
  const [open, setOpen] = React.useState(false);
  const [showSuccessPopup, setShowSuccessPopup] = React.useState(false);
  const [showErrorPopup, setShowErrorPopup] = React.useState(false);
  const [addButtonIsLoading, setAddButtonIsLoading] = React.useState(false);
  const axiosPrivate = useAxiosPrivate();

  const [scroll, setScroll] = React.useState("paper");
  const [auditlist, setAuditList] = React.useState([]);
  // const [selectedId, setSelectedId] = React.useState(0);
  const { user } = useSelector((store) => store.auth);
  const [msg, setMsg] = useState("");

  const {t,i18n} = useTranslation();

  const handleClickOpen = (scrollType) => () => {
    setOpen(true);
    setScroll("paper");
  };

  const handleClose = () => {
    // setSelectedId("");
    setOpen(false);
  };

  async function handleAdd() {
    const url = `${BASE_URL}/box/auditSet/updateAuditSetsForItemId/${props.reduxdata.id}`;

    // const headers = {
    //   "Content-Type": "application/json",
    //   Authorization: "Bearer " + user.jwtToken,
    // };

    const requestBody = {
      itemName: props.reduxdata.name,
      auditSetLists: [...auditlist],
    };

    setAddButtonIsLoading(true);

    axiosPrivate
      .put(url, requestBody)
      .then(async (response) => {
        setOpen(false);
        setMsg(response.data.message);
        setShowSuccessPopup(true);
      })
      .catch((error) => {
        setMsg(error.message);
        setOpen(false);
        setShowErrorPopup(true);
      })
      .finally(() => {
        setAddButtonIsLoading(false);
      });

    // const response = await addAuditSet(
    //   user.jwtToken,
    //   selectedId,
    //   props.reduxdata.id,
    //   props.reduxdata.name,
    //   props.reduxdata.type,
    //   props.reduxdata.createdAt
    // );

    // if (response.ok) {
    //   const data = await response.json();
    //   setOpen(false);
    //   setMsg(data.message);
    //   setShowSuccessPopup(true);
    // } else {
    //   const data = await response.json();
    //   setMsg(data.message);
    //   setOpen(false);
    //   setShowErrorPopup(true);
    // }
  }

  async function check() {
    const auditListUrl = `${BASE_URL}/box/auditSet/getMyAuditSetListForItemId/${props.reduxdata.id}`;

    const response = await axiosPrivate.get(auditListUrl);

    // const response = await getAuditSetList(user.jwtToken, props.reduxdata.id);
    if (response.status === 200) {
      // const data = await response.json();
      console.log("DATA :: ", response.data.data);
      setAuditList(response.data.data);
    } else {
      console.log("Error fetching VersionHistory data", response);
    }
  }

  const toggleSelected = (item) => {
    setAuditList((prevAuditSets) => {
      return prevAuditSets.map((auditSet) => {
        if (auditSet.auditSetId === item.auditSetId) {
          return {
            ...auditSet,
            isItemIdAdded: !auditSet.isItemIdAdded,
          };
        }
        return auditSet;
      });
    });
  };

  const descriptionElementRef = React.useRef(null);
  React.useEffect(() => {
    if (open) {
      check();
    }

    if (open) {
      const { current: descriptionElement } = descriptionElementRef;
      if (descriptionElement !== null) {
        descriptionElement.focus();
      }
    }
  }, [open]);

  return (
    <React.Fragment>
      <button
        id="btn1"
        onClick={handleClickOpen("paper")}
        style={{
          width: "191px",
          height: "45px",
          borderRadius: "100px",
          padding: "10px, 25px, 10px, 25px",
          color: "white",
          fontSize: 18,
        }}
        onMouseEnter={(e) => (e.target.style.backgroundColor = "#0061D0")}
        onMouseLeave={(e) => (e.target.style.backgroundColor = "#0061D5")}
      >
       {t("rightClickSecondFolderAddAuditText")}
      </button>

      {open && (
        <Dialog
          open={open}
          onClose={handleClose}
          scroll={scroll}
          aria-labelledby="scroll-dialog-title"
          aria-describedby="scroll-dialog-description"
          height="400px"
        >
          <DialogTitle id="scroll-dialog-title">
            <div
              style={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
              }}
            >
              <h2>{t("rightClickMainFileAuditSetsText")}</h2>
              <ClearIcon onClick={handleClose}></ClearIcon>
            </div>
          </DialogTitle>

          {/* <DialogContent dividers={scroll === "paper"}> */}
          <DialogContent>
            <DialogContentText
              id="scroll-dialog-description"
              ref={descriptionElementRef}
              tabIndex={-1}
            >
              {/* <div
                style={{
                  width: "400px",
                  height: "340px",
                  borderTopLeftRadius: "20px",
                  borderTopRightRadius: "20px",
                }}
              > */}
                {/* <div style={{ overflow: "auto", maxHeight: "380px",width:"300px"}}> */}
  <table style={{ tableLayout: "fixed",borderCollapse:"collapse",borderStyle:"hidden",width:"440px" }}>
    <thead>
      <tr style={{ height: "45px" }}>
        <th
          style={{
            color: "white",
            backgroundColor: "#0061D0",
            paddingLeft: "35px",
            textAlign: "left",
            width: "30%",
            borderTopLeftRadius: "20px",
          }}
        >
          <div style={{width:"100%",whiteSpace:"nowrap"}}>{t("rightClickPopUpTableSelectText")}</div>
          
        </th>
        <th
          style={{
            color: "white",
            backgroundColor: "#0061D0",
            // paddingLeft: "25px",
            textAlign: "left",
            width: "180%",
            borderTopRightRadius: "20px",
            paddingLeft:"80px"
          }}
        >
          
          {t("rightClickPopUpTableNameText")}
          
        </th>
      </tr>
    </thead>
    <tbody style={{ overflowY: "auto", display: "block", height:"362px",minWidth:"700%"}}>

    {auditlist.length === 0 && (
        <div style={{ height: '362px', display: 'flex', justifyContent: 'center', alignItems: 'center', textAlign: 'center',fontWeight:'bold' }}>
        {t("manageAuditSetEmptyListText")}
      </div>      
      )}

    {auditlist &&
  auditlist.map((item, index) => (
    <tr key={index} style={{ height: "45px", borderBottom: index === auditlist.length - 1 ? "1px solid #e7e7e7" : "none" }}>
      <td
        style={{
          color: "black",
          textAlign: "left",
          paddingLeft: "35px",
          borderLeft:"1px solid #e7e7e7",
          display: "table-cell",
          verticalAlign: "middle",
        }}
      >
        <Checkbox
          icon={<RadioButtonUncheckedIcon />}
          checkedIcon={<CheckCircleIcon />}
          checked={item.isItemIdAdded}
          onChange={(event) => {
            toggleSelected(item);
            console.log(item, "EVENT :: ", event);
          }}
          size="small"
        />
      </td>
      <td
        style={{
          color: "black",
          textAlign: "left",
          paddingLeft: "25px",
          width:"270%",
          display: "table-cell",
          verticalAlign: "middle",
          borderRight:"1px solid #e7e7e7",
          // overflowWrap: "break-word",
          wordWrap:"break-word"
        }}
      ><div style={{width:"90%",paddingLeft:"40px"}}>
        {/* <p>aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</p> */}
        {item.auditSetName ? item.auditSetName : "NA"}
      </div>
        {/* {item.auditSetName ? item.auditSetName : "NA"} */}
      </td>
    </tr>
  ))}

      
    </tbody>
  </table>
{/* </div> */}

              {/* </div> */}
            </DialogContentText>
          </DialogContent>

          <DialogActions>
            {/* <Button onClick={handleClose}>Cancel</Button> */}
            {/* <Button
              onClick={handleAdd}
              // disabled={!true || !selectedId}
              style={{
                color: "white",
                marginRight: "30px",
                backgroundColor: false ? "rgba(0,97,213,0.6)" : "#0061D5",
              }}
            >
              Add
            </Button> */}
            <div style={{marginRight:"20px",marginBottom:"10px"}}>
            <LoadingButton
              size="small"
              onClick={handleAdd}
              endIcon={
                <div
                  style={{
                    height: "20px",
                    width: addButtonIsLoading ? "20px" : "0px",
                  }}
                ></div>
              }
              loading={addButtonIsLoading}
              loadingPosition="end"
              variant="contained"
            >
              <span>{t("rightClickAddButtonText")}</span>
            </LoadingButton>
            </div>
          </DialogActions>
        </Dialog>
      )}

      {showSuccessPopup && (
        <SuccessPopUp
          iconColor="primary"
          iconSize={50}
          title={msg}
          open={true}
          handleClose={() => setShowSuccessPopup(false)}
        />
      )}
      {showErrorPopup && (
        <ErrorPopup
          iconColor="error"
          iconSize={50}
          title={msg}
          open={true}
          handleClose={() => setShowErrorPopup(false)}
        />
      )}
    </React.Fragment>
  );
}
