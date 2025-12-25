/* eslint-disable react/prop-types */
import "./Root.css";
import { useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import FileIcon from "../../Icons/FileIcon";
import FolderIcon from "../../Icons/FolderIcon";
import { BASE_URL, intersection1 } from "../../utils/constant";
import { Checkbox, FormControlLabel } from "@mui/material";
import { newaddToDenaiList, newremoveFromDenaiList } from "./auditFolderSlice";
import useAxiosPrivate from "../../hooks/useAxiosPrivate";

export default function RootTest({
  item,
  setSubChild,
  isRootAllowed,
  isRoot,

  removeRoot,
  parentItem,
}) {
  const { user } = useSelector((store) => store.auth);

  const [itemDetails, setItemdetails] = useState([]);
  const axiosPrivate = useAxiosPrivate();

  const refSet = useRef(new Map());

  const dispatch = useDispatch();
  const { allowedList } = useSelector((store) => store.folder);
  // console.log("newdeniedList :: ", newdeniedList);

  const allowedListMap = new Map();

  [...allowedList].forEach((item) => {
    allowedListMap.set(item.id, item);
  });
  // const isRootAllowed = allowedListMap.has(itemdata.id);

  const deniedSet = new Set(allowedList);

  // const isDenied = deniedSet.has(item);

  const allowed = intersection1(new Set([...itemDetails]), deniedSet); // deniedSet.intersection(itemDetails);

  const isAllowed = isRootAllowed && allowed.size === itemDetails.length;
  let isDenied =
    isRootAllowed && itemDetails.length === 0 ? true : allowed.size !== 0;

  if (item.id === 248804255060) {
    console.log("ALLOWED**************", allowed);
    console.log(
      "allowedList",
      allowedList,
      "Allowed  SIZE:: ",
      allowed.size,
      "ITEMS SIZE :: ",
      itemDetails.length
    );

    console.log("isAllowed :: ", isAllowed, "isDenied :: ", isDenied);
  }
  let checked = [isAllowed, isDenied];

  const subChild = (subChildList) => {
    [...subChildList].forEach((item) => {
      refSet.current.set(item.id, item);
    });

    console.log("FOLDER NAME :: ", item.name, "REF ::", refSet.current);
    let list = [];
    refSet.current.forEach((item) => {
      list.push(item);
    });

    itemDetails.forEach((item) => {
      list.push(item);
    });
    setSubChild(list);
  };

  async function fetchItem() {
    if (item.type === "file") {
      return;
    }
    console.log("before fetchItem");
    if (itemDetails.length <= 0) {
      console.log("fetchItem", item.id, user.jwtToken);
      const headers = new Headers();
      headers.append("Content-Type", "application/json");
      headers.append(
        "Authorization",
        `Bearer ${encodeURIComponent(user.jwtToken)}`
      );

      const url = `${BASE_URL}/box/folder/getItemList?folderId=${encodeURIComponent(
        item.id
      )}`;
      const response = await axiosPrivate.get(url);
      // const response = await fetch(url, {
      //   method: "GET",
      //   headers: headers,
      // });

      if (response.status === 200) {
        const { folderDetailsDtoList } = { ...response.data.data };
        // const mapData = new Map();
        // [...folderDetailsDtoList].map((item) => {
        //   mapData[item.id] = item;
        // });
        // setItemdetails(mapData);
        setItemdetails([...folderDetailsDtoList]);
        setSubChild([...folderDetailsDtoList]);
        if (isRootAllowed) {
          dispatch(newaddToDenaiList([...folderDetailsDtoList]));
        }
      }
    }
  }

  //   let checked = [false, false];

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
            <summary className="">
              <FormControlLabel
                sx={{ marginLeft: "1px" }}
                label={`${item.name} ${item.id}`}
                control={
                  <Checkbox
                    // sx={{
                    //   "& .MuiSvgIcon-root": {
                    //     fontSize: 22,
                    //   },
                    // }}
                    // checked={isDenied}
                    checked={checked[0] && checked[1]}
                    indeterminate={checked[0] !== checked[1]}
                    onChange={(e) => {
                      let list = [];
                      refSet.current.forEach((item) => {
                        list.push(item);
                      });

                      list.push(item);

                      itemDetails.forEach((item) => {
                        list.push(item);
                      });
                      if (isRoot) {
                        if (e.target.checked) {
                          dispatch(newaddToDenaiList(list));
                        } else {
                          if (removeRoot) {
                            list.push({ id: parentItem.id });
                          }
                          dispatch(newremoveFromDenaiList(list));
                        }
                      } else {
                        if (!allowedListMap.has(parentItem.id)) return;

                        if (e.target.checked) {
                          dispatch(newaddToDenaiList(list));
                        } else {
                          if (removeRoot) {
                            list.push({ id: parentItem.id });
                          }
                          dispatch(newremoveFromDenaiList(list));
                        }
                      }
                    }}
                  />
                }
                onClick={(e) => e.stopPropagation()}
              />
            </summary>

            {itemDetails && (
              <div className="align-right">
                <ul>
                  {[...itemDetails].map((i, index) => {
                    return (
                      <li key={index}>
                        <RootTest
                          item={i}
                          setSubChild={subChild}
                          isRootAllowed={allowedListMap.has(i.id)}
                          isRoot={false}
                          removeRoot={allowed.size === 1}
                          parentItem={item}
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
            label={`${item.name}  ${item.id}`}
            control={
              <Checkbox
                checked={isAllowed}
                onChange={(e) => {
                  let list = [];
                  list.push(item);

                  if (isRoot) {
                    if (e.target.checked) {
                      dispatch(newaddToDenaiList(list));
                    } else {
                      // if (removeRoot) {
                      //   list.push({ id: item.id });
                      // }
                      dispatch(newremoveFromDenaiList(list));
                    }
                  } else {
                    if (!allowedListMap.has(parentItem.id)) return;

                    if (e.target.checked) {
                      dispatch(newaddToDenaiList(list));
                    } else {
                      console.log(
                        "ALLOWED****B",
                        removeRoot,
                        allowedList,
                        item.id,
                        parentItem.id
                      );
                      if (removeRoot) {
                        list.push({ id: parentItem.id });
                      }
                      dispatch(newremoveFromDenaiList(list));
                    }
                  }
                }}
              />
            }
          />
        )}
      </div>
    </>
  );
}
