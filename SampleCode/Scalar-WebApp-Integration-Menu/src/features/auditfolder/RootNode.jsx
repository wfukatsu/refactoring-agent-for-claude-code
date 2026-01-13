/* eslint-disable react/prop-types */
import "./Root.css";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import FileIcon from "../../Icons/FileIcon";
import FolderIcon from "../../Icons/FolderIcon";
import { BASE_URL } from "../../utils/constant";
import { Checkbox, FormControlLabel } from "@mui/material";
import SubRoot from "./SubRoot";
import { toggleAllowedListFromRoot } from "./auditFolderSlice";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

export default function RootNode({ item }) {
  const { user } = useSelector((store) => store.auth);
  const [itemDetails, setItemdetails] = useState([]);
  const dispatch = useDispatch();
  const { allowedList, partiallyAllowedItems } = useSelector(
    (store) => store.folder
  );
  const axiosPrivate = useAxiosPrivate();

  console.log("itemDetails==", allowedList);

  const isAllowed = allowedList.length === itemDetails.length;
  const isDenied = allowedList.length !== 0;

  console.log("isAllowed :: ", isAllowed, "isDenied :: ", isDenied);

  let checked = [isAllowed, isDenied];

  // if (item.name === "DEMO Two") {
  //   console.log(
  //     "NAME :: ",
  //     item.name,
  //     "ROOT-ALLOWED :: ",
  //     isRootAllowed,
  //     "IS-DENIED :: ",
  //     isDenied
  //   );
  // }

  async function fetchItem() {
    if (item.type === "file") {
      return;
    }
    console.log("before fetchItem");
    if (itemDetails.length <= 0) {
      console.log("fetchItem", item.id, user.jwtToken);
      // const headers = new Headers();
      // headers.append("Content-Type", "application/json");
      // headers.append(
      //   "Authorization",
      //   `Bearer ${encodeURIComponent(user.jwtToken)}`
      // );

      const url = `${BASE_URL}/box/folder/getItemList?folderId=${encodeURIComponent(
        item.id
      )}`;
      // const response = await fetch(url, {
      //   method: "GET",
      //   headers: headers,
      // });
      const response = await axiosPrivate.get(url);
      if (response.status === 200) {
        const { folderDetailsDtoList, size } = { ...response.data.data };
        console.log(
          "folderDetailsDtoList :: ",
          folderDetailsDtoList,
          "SIZE  :: ",
          size
        );
        setItemdetails([...folderDetailsDtoList]);
      }
      // setItemdetails("ss");
    }
  }

  return (
    <>
      <div className="node-item">
        {item.type === "folder" ? (
          <FolderIcon className="folder" style={{ color: "#0061D5" }} />
        ) : (
          <FileIcon className="folder" />
        )}
        <details open={false} onClick={fetchItem}>
          <summary>
            {item.type === "folder" && (
              <FormControlLabel
                label={`${item.name}`}
                control={
                  <Checkbox
                    checked={checked[0] && checked[1]}
                    indeterminate={checked[0] !== checked[1]}
                    onChange={(e) => {
                      dispatch(
                        toggleAllowedListFromRoot(itemDetails, e.target.checked)
                      );
                    }}
                  />
                }
              />
            )}
          </summary>

          {itemDetails && (
            <div className="align-right">
              <ul>
                {itemDetails.map((item, index) => {
                  return (
                    <li key={index}>
                      <SubRoot
                        item={item}
                        isRootAllowed={isDenied}
                        isRoot={false}
                        rootItemId={item.id}
                      />
                      {/* <Node
                        item={item}
                        isRootAllowed={isDenied}
                        isRoot={false}
                        rootItemId={item.id}
                      /> */}
                    </li>
                  );
                })}
              </ul>
            </div>
          )}
        </details>
      </div>
    </>
  );
}
