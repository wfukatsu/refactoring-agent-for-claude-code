/* eslint-disable react/prop-types */
import "./Root.css";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import FileIcon from "../../Icons/FileIcon";
import FolderIcon from "../../Icons/FolderIcon";
import { BASE_URL } from "../../utils/constant";
import { Checkbox, FormControlLabel } from "@mui/material";
import Node from "./Node";
import { toggleAllowedList } from "./auditFolderSlice";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

export default function SubRoot({ item, isRootAllowed }) {
  const { user } = useSelector((store) => store.auth);
  const [itemDetails, setItemdetails] = useState([]);
  const dispatch = useDispatch();
  const { allowedList, partiallyAllowedItems } = useSelector(
    (store) => store.folder
  );

  const axiosPrivate = useAxiosPrivate();

  const isAllowed = allowedList.some((i) => i.itemId === item.id);

  async function fetchItem() {
    if (item.type === "file") {
      return;
    }
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
        console.log(response.data);
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

  let checked = [true, true];

  return (
    <>
      <div className="node-item">
        {item.type === "folder" ? (
          <FolderIcon className="folder" style={{ color: "#0061D5" }} />
        ) : (
          <FileIcon className="folder" />
        )}

        {item.type === "folder" ? (
          <details open={false} onClick={fetchItem}>
            <summary>
              <FormControlLabel
                label={`${item.name}`}
                control={
                  <Checkbox
                    checked={isAllowed}
                    // indeterminate={checked[0] !== checked[1]}
                    onChange={(e) => {
                      dispatch(toggleAllowedList(item, e.target.checked));
                    }}
                  />
                }
              />
            </summary>

            {itemDetails && (
              <div className="align-right">
                <ul>
                  {itemDetails.map((i, index) => {
                    return (
                      <li key={index}>
                        <Node
                          item={i}
                          isRootAllowed={true}
                          subRootItemId={item.id}
                        />
                      </li>
                    );
                  })}
                </ul>
              </div>
            )}
          </details>
        ) : (
          <FormControlLabel
            label={`${item.name}`}
            control={
              <Checkbox
                checked={isAllowed}
                // indeterminate={checked[0] !== checked[1]}
                onChange={(e) => {
                  dispatch(toggleAllowedList(item, e.target.checked));
                }}
              />
            }
          />
        )}
      </div>
    </>
  );
}
